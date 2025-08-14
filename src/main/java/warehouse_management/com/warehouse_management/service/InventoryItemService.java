package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemPoNumberDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseDto;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemProductionVehicleTypeDto;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.InventoryTransferProductionDepartureDto.InventoryItemTransfer;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final WarehouseService warehouseService;
    private final WarehouseTransferTicketService warehouseTransferTicketService;

    public InventoryItem createInventoryItem(CreateInventoryItemDto req) {
        InventoryItem item = mapper.toInventoryItemModel(req);
        // Lưu DB
        return inventoryItemRepository.save(item);
    }

    public InventoryItem getItemToId(ObjectId id){
        InventoryItem inventoryItem = inventoryItemRepository.findById(id).orElse(null);
        if(inventoryItem == null || inventoryItem.getDeletedAt() != null)
            throw LogicErrException.of("Mặt hàng không tồn tại.");
        return inventoryItem;
    }

    public PageInfoDto<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(String warehouseId, PageOptionsDto optionsReq) {
        Page<InventoryItemProductionVehicleTypeDto> itemsPageObject = inventoryItemRepository.getItemsFromVehicleWarehouse(
                new ObjectId(warehouseId),
                optionsReq);
        PageInfoDto<InventoryItemProductionVehicleTypeDto> response = new PageInfoDto<>(itemsPageObject);
        return response;
    }

    public List<InventoryPoWarehouseDto> getInventoryInStockPoNumbers(String warehouseType, String inventoryType) {
        return inventoryItemRepository.findPoNumbersOfInventoryInStock(warehouseType, inventoryType);
    }

    public List<InventoryItemPoNumberDto> getInventoryInStockByPoNumber(String warehouseType, String poNumber, String filter){
        return inventoryItemRepository.findInventoryInStockByPoNumber(warehouseType, poNumber, filter);
    }

    @Transactional
    public Warehouse transferItemsProductionToDeparture(InventoryTransferProductionDepartureDto req) {
        Warehouse warehouseDeparture = warehouseService.getWarehouseToId(new ObjectId(req.getDepartureWarehouseId()));
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
            // Mã Container, Trạng thái Cont, Ngày đi, Ngày đến giữ nguyên rỗng
            LocalDateTime arrivalDate = LocalDate.parse(req.getArrivalDate()).atStartOfDay();
            List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
            for(var item : itemsToTransfer){
                if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                    int quantityToTransfer = itemIdQualityMap.get(item.getId().toString());
                    if(item.getQuantity() == 0)
                        throw LogicErrException.of("Hàng phụ tùng " + item.getProductCode() + " hiện hết hàng.");
                    else if(item.getQuantity() < quantityToTransfer)
                        throw LogicErrException.of("Số lượng phụ tùng " + item.getProductCode() + " cần nhập vượt quá số lượng trong kho.");
                    else if(item.getQuantity() > quantityToTransfer){
                        InventoryItem sparePartToDeparture = mapper.cloneEntity(item);
                        sparePartToDeparture.setId(null);
                        sparePartToDeparture.setQuantity(quantityToTransfer);
                        // Kho hiện tại → “Kho đi (TQ)”
                        sparePartToDeparture.setWarehouseId(warehouseDeparture.getId());
                        sparePartToDeparture.setStatus(InventoryItemStatus.IN_TRANSIT);
                        // Ngày giao hàng = ngày đã chọn theo PO
                        sparePartToDeparture.getLogistics().setArrivalDate(arrivalDate);
                        itemsSparePartToNew.add(sparePartToDeparture);
                        item.setQuantity(item.getQuantity() - quantityToTransfer);
                        continue;
                    }
                }

                // Kho hiện tại → “Kho đi (TQ)”
                item.setWarehouseId(warehouseDeparture.getId());
                item.setStatus(InventoryItemStatus.IN_TRANSIT);
                // Ngày giao hàng = ngày đã chọn theo PO
                item.getLogistics().setArrivalDate(arrivalDate);
            }
            inventoryItemRepository.insertAll(itemsSparePartToNew);
            inventoryItemRepository.bulkUpdateTransferDeparture(itemsToTransfer);

            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)

            return warehouseDeparture;
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Nhập hàng sang kho "+warehouseDeparture.getName()+" thất bại, hãy thử lại.");
        }
    }

    @Transactional
    public Warehouse transferItemsDestinationToConsignment(@RequestBody InventoryTransferDestinationConsignmentDto dto){
        Warehouse warehouseConsignment = warehouseService.getWarehouseToId(new ObjectId(dto.getConsignmentWarehouseId()));
        if(!warehouseConsignment.getType().equals(WarehouseType.CONSIGNMENT))
            throw LogicErrException.of("Kho cần nhập hàng không phải là kho đi.");
        if(dto.getInventoryItems().isEmpty())
            throw LogicErrException.of("Hàng hóa cần nhập sang kho đi hiện đang rỗng.");

        try{
            // Hệ thống bắt đầu một giao dịch (transaction)
            Map<String, Integer> itemIdQualityMap = dto.getInventoryItems().stream().collect(
                    Collectors.toMap(InventoryTransferDestinationConsignmentDto.InventoryItemTransfer::getId, InventoryTransferDestinationConsignmentDto.InventoryItemTransfer::getQuantity)
            );
            List<ObjectId> itemIdToTransfer = itemIdQualityMap.keySet().stream().map(ObjectId::new).toList();
            // Lấy toàn bộ sản phẩm (theo mã sản phẩm) trong Kho chờ sản xuất có PO được chọn
            List<InventoryItem> itemsToTransfer = inventoryItemRepository.findByIdIn(itemIdToTransfer);
            // Mã Container, Trạng thái Cont, Ngày đi, Ngày đến giữ nguyên rỗng
            LocalDateTime consignmentDate = LocalDate.parse(dto.getConsignmentDate()).atStartOfDay();
            List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
            for(var item : itemsToTransfer){
                if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                    int quantityToTransfer = itemIdQualityMap.get(item.getId().toString());
                    if(item.getQuantity() == 0)
                        throw LogicErrException.of("Hàng phụ tùng " + item.getProductCode() + " hiện hết hàng.");
                    else if(item.getQuantity() < quantityToTransfer)
                        throw LogicErrException.of("Số lượng phụ tùng " + item.getProductCode() + " cần nhập vượt quá số lượng trong kho.");
                    else if(item.getQuantity() > quantityToTransfer){
                        InventoryItem sparePartToDeparture = mapper.cloneEntity(item);
                        sparePartToDeparture.setId(null);
                        sparePartToDeparture.setQuantity(quantityToTransfer);
                        // Kho hiện tại → “Kho đi (TQ)”
                        sparePartToDeparture.setWarehouseId(warehouseConsignment.getId());
                        sparePartToDeparture.setStatus(InventoryItemStatus.IN_TRANSIT);
                        // Ngày giao hàng = ngày đã chọn theo PO
                        sparePartToDeparture.getLogistics().setConsignmentDate(consignmentDate);
                        itemsSparePartToNew.add(sparePartToDeparture);
                        item.setQuantity(item.getQuantity() - quantityToTransfer);
                        continue;
                    }
                }

                // Kho hiện tại → “Kho ký gửi”
                item.setWarehouseId(warehouseConsignment.getId());
                item.setStatus(InventoryItemStatus.IN_TRANSIT);
                // Ngày giao hàng = ngày đã chọn theo PO
                item.getLogistics().setConsignmentDate(consignmentDate);
            }
            inventoryItemRepository.insertAll(itemsSparePartToNew);
            inventoryItemRepository.bulkUpdateTransferDeparture(itemsToTransfer);

            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)

            return warehouseConsignment;
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Nhập hàng sang kho "+warehouseConsignment.getName()+" thất bại, hãy thử lại.");
        }
    }

    @Transactional
    public List<InventoryItem> bulkCreateInventoryItems(List<InventoryItemCreateDto> createDtos) {
        if (createDtos == null || createDtos.isEmpty()) {
            return List.of();
        }

        ObjectId currentUserId = new ObjectId("6897243be68a83bcaf7c0d16");

        List<InventoryItem> itemsToInsert = createDtos.stream()
                .map(dto -> {
                    InventoryItem item = new InventoryItem();
                    item.setPoNumber(dto.getPoNumber());
                    item.setProductCode(dto.getProductCode());
                    item.setSerialNumber(dto.getSerialNumber());
                    item.setModel(dto.getModel());
                    item.setType(dto.getType());
                    item.setCategory(dto.getCategory());
                    item.setInventoryType(dto.getInventoryType());
                    item.setQuantity(dto.getQuantity());
                    item.setWarehouseId(new ObjectId(dto.getWarehouseId()));

                    item.setStatus(InventoryItemStatus.IN_STOCK.getId());

                    item.setInitialCondition(dto.getInitialCondition());
                    item.setNotes(dto.getNotes());

                    return item;
                })
                .collect(Collectors.toList());

        return inventoryItemRepository.insert(itemsToInsert);
    }

    @Transactional
    public Map<String, Object> stockTransfer(InventoryStockTransferDto req) {
        Warehouse originWarehouse = warehouseService.getWarehouseToId(new ObjectId(req.getOriginWarehouseId()));
        Warehouse destinationWarehouse = warehouseService.getWarehouseToId(new ObjectId(req.getDestinationWarehouseId()));
        try{
            // Hệ thống bắt đầu một giao dịch (transaction)
            Map<String, Integer> itemIdQualityMap = req.getInventoryItems().stream().collect(
                    Collectors.toMap(InventoryStockTransferDto.InventoryItemTransfer::getId, InventoryStockTransferDto.InventoryItemTransfer::getQuantity)
            );
            List<ObjectId> itemIdToTransfer = itemIdQualityMap.keySet().stream().map(ObjectId::new).toList();
            // Lấy toàn bộ sản phẩm (theo mã sản phẩm) trong Kho chờ sản xuất có PO được chọn
            List<InventoryItem> itemsToTransfer = inventoryItemRepository.findByIdIn(itemIdToTransfer);
            List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
            List<InventoryItem> itemsToApproval = new ArrayList<>();
            for(var item : itemsToTransfer){
                if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                    int quantityToTransfer = itemIdQualityMap.get(item.getId().toString());
                    if(item.getQuantity() == 0)
                        throw LogicErrException.of("Hàng phụ tùng " + item.getProductCode() + " hiện hết hàng.");
                    else if(item.getQuantity() < quantityToTransfer)
                        throw LogicErrException.of("Số lượng phụ tùng " + item.getProductCode() + " cần nhập vượt quá số lượng trong kho.");
                    else if(item.getQuantity() > quantityToTransfer){
                        InventoryItem sparePartToDeparture = mapper.cloneEntity(item);
                        sparePartToDeparture.setId(null);
                        sparePartToDeparture.setQuantity(quantityToTransfer);
                        // Kho hiện tại → “Kho đích”
                        sparePartToDeparture.setWarehouseId(destinationWarehouse.getId());
                        sparePartToDeparture.setStatus(InventoryItemStatus.IN_TRANSIT);
                        itemsSparePartToNew.add(sparePartToDeparture);
                        item.setQuantity(item.getQuantity() - quantityToTransfer);
                        continue;
                    }
                }

                // Kho hiện tại → “Kho đích”
                item.setWarehouseId(destinationWarehouse.getId());
                item.setStatus(InventoryItemStatus.IN_TRANSIT);
                itemsToApproval.add(item);
            }
            itemsSparePartToNew = inventoryItemRepository.insertAll(itemsSparePartToNew);
            inventoryItemRepository.bulkUpdateTransferDeparture(itemsToTransfer);
            itemsToApproval.addAll(itemsSparePartToNew);
            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)

            // Tạo phiếu duyệt
            WarehouseTransferTicket ticket = warehouseTransferTicketService.createAndSendMessage(originWarehouse, destinationWarehouse, itemsToApproval);
            return Map.of(
                    "ticketId", ticket.getId(),
                    "originWarehouse",  originWarehouse,
                    "destinationWarehouse", destinationWarehouse
            );
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Nhập hàng sang kho "+destinationWarehouse.getName()+" thất bại, hãy thử lại.");
        }
    }

    @Transactional
    public long deleteToId(String id){
        // TODO: GET current User

        // Xóa sản phẩm
        InventoryItem inventoryItem = getItemToId(new ObjectId(id));
        return inventoryItemRepository.softDelete(inventoryItem.getId(), null);
    }

    @Transactional
    public long deleteBulk(List<String> inventoryItemIds){
        // TODO: GET current User

        // Xóa nhóm sản phẩm
        List<ObjectId> ids = inventoryItemIds.stream().map(ObjectId::new).collect(Collectors.toList());
        return inventoryItemRepository.bulkSoftDelete(ids, null);
    }


    @Transactional
    public InventoryItem updateInventoryItem(UpdateInventoryItemDto dto){
        InventoryItem inventoryItem = getItemToId(new ObjectId(dto.getId()));
        mapper.mapToUpdateInventoryItem(inventoryItem, dto);
        return inventoryItemRepository.save(inventoryItem);
    }
}
