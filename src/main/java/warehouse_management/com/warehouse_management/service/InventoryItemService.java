package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.common.pagination.res.PageInfoRes;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.CreateInventoryItemReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryTransferWarehouseReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseRes;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemProductionVehicleTypeDto;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryTransferWarehouseReq.InventoryItemTransfer;


@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final WarehouseService warehouseService;
    private final ModelMapper modelMapper;

    public InventoryItem createInventoryItem(CreateInventoryItemReq req) {
        InventoryItem item = mapper.toInventoryItemModel(req);
        // Lưu DB
        return inventoryItemRepository.save(item);
    }

    public PageInfoRes<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(String warehouseId, PageOptionsReq optionsReq) {
        Page<InventoryItemProductionVehicleTypeDto> itemsPageObject = inventoryItemRepository.getItemsFromVehicleWarehouse(
                new ObjectId(warehouseId),
                optionsReq);
        PageInfoRes<InventoryItemProductionVehicleTypeDto> response = new PageInfoRes<>(itemsPageObject);
        return response;
    }

    public List<InventoryPoWarehouseRes> getInventoryInStockPoNumbers(String warehouseType, String filter, List<String> sortBy, Sort.Direction direction){
        Sort sort = Sort.unsorted();
        if(sortBy != null && !sortBy.isEmpty() && direction != null)
            sort = Sort.by(direction, sortBy.toArray(String[]::new));
        return inventoryItemRepository.findInventoryInStockPoNumbers(warehouseType, filter, sort);
    }

    public List<InventoryItem> getInventoryInStockByPoNumber(String warehouseType, String poNumber, String filter, List<String> sortBy, Sort.Direction direction){
        Sort sort = Sort.unsorted();
        if(sortBy != null && !sortBy.isEmpty() && direction != null)
            sort = Sort.by(direction, sortBy.toArray(String[]::new));
        return inventoryItemRepository.findInventoryInStockByPoNumber(warehouseType, poNumber, filter, sort);
    }

    @Transactional
    public Warehouse transferItemsProductionToDeparture(InventoryTransferWarehouseReq req) {
        Warehouse warehouseDeparture = warehouseService.getWarehouseToId(new ObjectId(req.getToWarehouseId()));
        if(!warehouseDeparture.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kho cần nhập hàng không phải là kho đi.");
        if(req.getInventoryItems().isEmpty())
            throw LogicErrException.of("Hàng hóa cần nhập sang kho đi hiện đang rỗng.");

        try{
            // Hệ thống bắt đầu một giao dịch (transaction)
            Map<String, Integer> itemIdQualityMap = req.getInventoryItems().stream().collect(
                    Collectors.toMap(InventoryItemTransfer::getId, InventoryItemTransfer::getQuantity)
            );
            List<ObjectId> itemIdToTransfer = itemIdQualityMap.keySet().stream().map(ObjectId::new).toList();
            // Lấy toàn bộ sản phẩm (theo mã sản phẩm) trong Kho chờ sản xuất có PO được chọn
            List<InventoryItem> itemsToTransfer = inventoryItemRepository.findByIdIn(itemIdToTransfer);
            List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
            // Tạo sẵn id cho container
            for(var item : itemsToTransfer){
                if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                    int quantityToTransfer = itemIdQualityMap.get(item.getId().toString());
                    if(item.getQuantity() == 0)
                        throw LogicErrException.of("Hàng phụ tùng " + item.getProductCode() + " hiện hết hàng.");
                    else if(item.getQuantity() < quantityToTransfer)
                        throw LogicErrException.of("Số lượng phụ tùng " + item.getProductCode() + " cần nhập vượt quá số lượng trong kho.");
                    else if(item.getQuantity() > quantityToTransfer){
                        InventoryItem sparePartToDeparture = modelMapper.map(item, InventoryItem.class);
                        sparePartToDeparture.setId(null);
                        sparePartToDeparture.setQuantity(quantityToTransfer);
                        // Kho hiện tại → “Kho đi (TQ)”
                        sparePartToDeparture.setWarehouseId(warehouseDeparture.getId());
                        sparePartToDeparture.setStatus(InventoryItemStatus.IN_TRANSIT);
                        // Ngày giao hàng = ngày đã chọn theo PO
                        sparePartToDeparture.getLogistics().setArrivalDate(LocalDate.parse(req.getArrivalDate()).atStartOfDay());
                        itemsSparePartToNew.add(sparePartToDeparture);
                        item.setQuantity(item.getQuantity() - quantityToTransfer);
                        continue;
                    }
                }

                // Kho hiện tại → “Kho đi (TQ)”
                item.setWarehouseId(warehouseDeparture.getId());
                item.setStatus(InventoryItemStatus.IN_TRANSIT);
                // Ngày giao hàng = ngày đã chọn theo PO
                item.getLogistics().setArrivalDate(LocalDate.parse(req.getArrivalDate()).atStartOfDay());
            }
            inventoryItemRepository.insertAll(itemsSparePartToNew);
            inventoryItemRepository.bulkUpdateTransferDeparture(itemsToTransfer);

            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)

            return warehouseDeparture;
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Nhập hàng sang kho đi thất bại, hãy thử lại.");
        }
    }

}
