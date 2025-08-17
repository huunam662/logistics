package warehouse_management.com.warehouse_management.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import warehouse_management.com.warehouse_management.annotation.AuditAction;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionProductDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket.WarehouseTransferTicketRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final WarehouseService warehouseService;
    private final WarehouseTransferTicketService warehouseTransferTicketService;
    private final InventoryItemMapper inventoryItemMapper;
    private final WarehouseTransferTicketRepository warehouseTransferTicketRepository;


    @Transactional
    public InventoryItem createInventorySparePart(CreateInventorySparePartDto req){
        InventoryItem item = inventoryItemMapper.toInventoryItemSparePart(req);
        item.setInventoryType(InventoryType.SPARE_PART.getId());
        if(item.getLogistics() == null) item.setLogistics(new InventoryItem.Logistics());
        try{
            item.getLogistics().setOrderDate(LocalDate.parse(req.getOrderDate()).atStartOfDay());
        }
        catch (Exception e){
            throw LogicErrException.of("Ngày đặt hàng phải đúng định dạng 'yyyy-MM-dd'");
        }
        return inventoryItemRepository.save(item);
    }

    public InventoryProductDetailsDto getInventoryProductDetails(ObjectId id){
        InventoryItem item = getItemToId(id);
        return mapper.toInventoryProductDetailsDto(item);
    }

    public InventorySparePartDetailsDto getInventorySparePartDetails(ObjectId id){
        InventoryItem item = getItemToId(id);
        return mapper.toInventorySparePartDetailsDto(item);
    }

    @Transactional
    public InventoryItem updateInventorySparePart(String id, CreateInventorySparePartDto req){
        InventoryItem item = getItemToId(new ObjectId(id));
        inventoryItemMapper.mapToUpdateInventorySparePart(item, req);
        if(item.getLogistics() == null) item.setLogistics(new InventoryItem.Logistics());
        try{
            item.getLogistics().setOrderDate(LocalDate.parse(req.getOrderDate()).atStartOfDay());
        }
        catch (Exception e){
            throw LogicErrException.of("Ngày đặt hàng phải đúng định dạng 'yyyy-MM-dd'");
        }
        return inventoryItemRepository.save(item);
    }

    @Transactional
    public InventoryItem createInventoryProduct(CreateInventoryProductDto req) {
        InventoryItem item = mapper.toInventoryItemModel(req);
        item.setQuantity(1); // Xe hoặc Phụ kiện mặc định là 1
        if(item.getLogistics() == null) item.setLogistics(new InventoryItem.Logistics());
        try{
            item.getLogistics().setOrderDate(LocalDate.parse(req.getLogistics().getOrderDate()).atStartOfDay());
        }
        catch (Exception e){
            throw LogicErrException.of("Ngày đặt hàng phải đúng định dạng 'yyyy-MM-dd'");
        }
        try{
            item.getLogistics().setEstimateCompletionDate(LocalDate.parse(req.getLogistics().getEstimateCompletionDate()).atStartOfDay());
        }
        catch (Exception e){
            throw LogicErrException.of("Ngày dự kiến SX xong phải đúng định dạng 'yyyy-MM-dd'");
        }
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

    public List<InventoryPoWarehouseDto> getInventoryInStockPoNumbers(String warehouseType, List<String> inventoryTypes, String poNumber, String warehouseId) {
        return inventoryItemRepository.findPoNumbersOfInventoryInStock(warehouseType, inventoryTypes, poNumber, warehouseId);
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
            LocalDateTime arrivalDate = LocalDate.parse(req.getArrivalDate()).atStartOfDay();
            transferItems(req.getInventoryItems(), warehouseDeparture.getId(), null, arrivalDate, null, InventoryItemStatus.IN_STOCK);

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
            LocalDateTime consignmentDate = LocalDate.parse(dto.getConsignmentDate()).atStartOfDay();
            transferItems(dto.getInventoryItems(), warehouseConsignment.getId(), null, null, consignmentDate, InventoryItemStatus.IN_STOCK);

            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)

            return warehouseConsignment;
        }
        catch (Exception e){
            if(e instanceof LogicErrException l) throw l;
            throw LogicErrException.of("Nhập hàng sang kho "+warehouseConsignment.getName()+" thất bại, hãy thử lại.");
        }
    }

    }

    @AuditAction(action = "CREATE_DCNB_TICKET")
    @Transactional
    public Map<String, Object> stockTransfer(InventoryStockTransferDto req) {
        WarehouseTransferTicket ticket = warehouseTransferTicketService.getTicketToId(new ObjectId(req.getTicketId()));
        Warehouse originWarehouse = warehouseService.getWarehouseToId(new ObjectId(req.getOriginWarehouseId()));
        Warehouse destinationWarehouse = warehouseService.getWarehouseToId(new ObjectId(req.getDestinationWarehouseId()));
        try{
            List<InventoryItem> itemsResults = transferItems(req.getInventoryItems(), destinationWarehouse.getId(), null, null, null, InventoryItemStatus.OTHER);
            ticket.setJsonPrint(warehouseTransferTicketService.buildJsonPrint(ticket, itemsResults));
            ticket.setInventoryItems(itemsResults.stream().map(inventoryItemMapper::toInventoryItemTicket).toList());
            warehouseTransferTicketRepository.save(ticket);
            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)

            return Map.of(
                        "ticketId", ticket.getId(),
                    "originWarehouse", originWarehouse,
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
    public InventoryItem updateInventoryProduct(String id, CreateInventoryProductDto dto){
        InventoryItem inventoryItem = getItemToId(new ObjectId(id));
        mapper.mapToUpdateInventoryProduct(inventoryItem, dto);
        try{
            inventoryItem.getLogistics().setOrderDate(LocalDate.parse(dto.getLogistics().getOrderDate()).atStartOfDay());
        }
        catch (Exception e){
            throw LogicErrException.of("Ngày đặt hàng phải đúng định dạng 'yyyy-MM-dd'");
        }
        try{
            inventoryItem.getLogistics().setEstimateCompletionDate(LocalDate.parse(dto.getLogistics().getEstimateCompletionDate()).atStartOfDay());
        }
        catch (Exception e){
            throw LogicErrException.of("Ngày dự kiến SX xong phải đúng định dạng 'yyyy-MM-dd'");
        }
        return inventoryItemRepository.save(inventoryItem);
    }

    @Transactional
    public List<InventoryItem> transferItems(List<InventoryItemTransferDto> items, ObjectId toWarehouseId, Container container, LocalDateTime arrivalDate, LocalDateTime consignmentDate, InventoryItemStatus itemStatus){
        // Hệ thống bắt đầu một giao dịch (transaction)
        Map<String, Integer> itemIdQualityMap = items.stream().collect(
                Collectors.toMap(InventoryItemTransferDto::getId, InventoryItemTransferDto::getQuantity)
        );
        List<ObjectId> itemIdToTransfer = itemIdQualityMap.keySet().stream().map(ObjectId::new).toList();
        // Lấy toàn bộ sản phẩm (theo mã sản phẩm) trong Kho chờ sản xuất có PO được chọn
        List<InventoryItem> itemsToTransfer = inventoryItemRepository.findByIdIn(itemIdToTransfer);
        List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
        List<InventoryItem> itemsResults = new ArrayList<>();
        for(var item : itemsToTransfer){
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                int quantityToTransfer = itemIdQualityMap.get(item.getId().toString());
                if(item.getQuantity() == 0)
                    throw LogicErrException.of("Hàng phụ tùng " + item.getProductCode() + " hiện hết hàng.");
                else if(item.getQuantity() < quantityToTransfer)
                    throw LogicErrException.of("Số lượng phụ tùng " + item.getProductCode() + " cần nhập vượt quá số lượng trong kho.");
                else if(item.getQuantity() > quantityToTransfer){
                    InventoryItem sparePartToDeparture = inventoryItemMapper.cloneEntity(item);
                    sparePartToDeparture.setId(null);
                    sparePartToDeparture.setQuantity(quantityToTransfer);
                    // Kho hiện tại → “Kho khác”
                    sparePartToDeparture.setWarehouseId(toWarehouseId);
                    sparePartToDeparture.setStatus(itemStatus.getId());
                    if(container != null){
                        sparePartToDeparture.setContainerId(container.getId());
                        sparePartToDeparture.getLogistics().setDepartureDate(container.getDepartureDate());
                    }
                    if(arrivalDate != null){
                        // Ngày giao hàng = ngày đã chọn theo PO
                        sparePartToDeparture.getLogistics().setArrivalDate(arrivalDate);
                    }
                    if(consignmentDate != null){
                        sparePartToDeparture.getLogistics().setConsignmentDate(consignmentDate);
                    }
                    itemsSparePartToNew.add(sparePartToDeparture);
                    item.setQuantity(item.getQuantity() - quantityToTransfer);
                    continue;
                }
            }

            // Kho hiện tại → “Kho khác”
            item.setWarehouseId(toWarehouseId);
            item.setStatus(itemStatus.getId());
            if(container != null){
                item.setContainerId(container.getId());
                item.getLogistics().setDepartureDate(container.getDepartureDate());
            }
            if(arrivalDate != null){
                // Ngày giao hàng = ngày đã chọn theo PO
                item.getLogistics().setArrivalDate(arrivalDate);
            }
            if(consignmentDate != null){
                item.getLogistics().setConsignmentDate(consignmentDate);
            }
            itemsResults.add(item);
        }

        inventoryItemRepository.insertAll(itemsSparePartToNew);
        inventoryItemRepository.bulkUpdateTransfer(itemsToTransfer);
        itemsResults.addAll(itemsSparePartToNew);
        return itemsResults;
    }

    public List<InventoryItem> bulkCreateProductionProductItems(List<ExcelImportProductionProductDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        List<InventoryItem> itemsToInsert = dtos.stream()
                .map(dto -> {
                    //DTO MAPPING
                    InventoryItem item = mapper.toInventoryItem(dto);
                    item.setStatus(InventoryItemStatus.IN_STOCK);
                    return item;
                })
                .collect(Collectors.toList());

        return inventoryItemRepository.insert(itemsToInsert);
    }

    public List<InventoryItem> bulkCreateProductionSparePartItems(List<ExcelImportProductionSparePartDto> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }
        List<InventoryItem> itemsToInsert = dtos.stream()
                .map(dto -> {
                    //DTO MAPPING
                    InventoryItem item = mapper.toInventoryItem(dto);
                    item.setStatus(InventoryItemStatus.IN_STOCK);
                    return item;
                })
                .collect(Collectors.toList());

        return inventoryItemRepository.insert(itemsToInsert);
    }


}
