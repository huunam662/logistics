package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import warehouse_management.com.warehouse_management.annotation.AuditAction;
import warehouse_management.com.warehouse_management.aspect.AuditContext;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportDestinationProductDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportDestinationSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionProductDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.enumerate.*;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.pojo.IdProjection;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
import warehouse_management.com.warehouse_management.repository.warehouse_transaction.WarehouseTransactionRepository;
import warehouse_management.com.warehouse_management.utils.GeneralResource;
import warehouse_management.com.warehouse_management.utils.TranUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryItemService {
    private final InventoryItemMapper mapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final WarehouseService warehouseService;
    private final WarehouseTransactionService warehouseTransferTicketService;
    private final InventoryItemMapper inventoryItemMapper;
    private final WarehouseTransactionRepository warehouseTransferTicketRepository;
    private final MongoTemplate mongoTemplate;
    private final TranUtils tranUtils;
    private final WarehouseRepository warehouseRepository;

//    NHẬP VÀO KHO BẰNG EXCEL HOẶC FORM

    @Transactional
    public InventoryItem createInventorySparePart(CreateInventorySparePartDto req) {
        Warehouse inStockWh = warehouseService.getWarehouseToId(new ObjectId(req.getWarehouseId()));
        LocalDate orderDate = parseOrderDate(req.getOrderDate());
        // 1. Chuyển đổi DTO sang InventoryItem
        InventoryItem newItem = buildSparePartItem(req, inStockWh, orderDate);
        InventoryItem saved = inventoryItemRepository.save(newItem);
        // 2. insert vào các kho destination khác
//        CHỈ CÓ PHỤ TÙNG MỚI INSERT VÀO CÁC KHO KHÁC
        if (inStockWh.getType() == WarehouseType.PRODUCTION) {
            inventoryItemRepository.bulkInsert(buildInventoryItemToOtherDestWh(List.of(newItem)));
        }
        // 3. Ghi phiếu nhập kho (WarehouseTransaction)
        warehouseTransferTicketRepository.save(buildAWarehouseTransaction(inStockWh, newItem));
        return saved;
    }

    @Transactional
    public InventoryItem createInventoryProduct(CreateInventoryProductDto req) {
        Warehouse inStockWh = warehouseService.getWarehouseToId(new ObjectId(req.getWarehouseId()));
        // 1. Chuyển đổi DTO sang InventoryItem
        InventoryItem newItem = buildProductItem(req, inStockWh);
        InventoryItem saved = inventoryItemRepository.save(newItem);

        // 3. Ghi phiếu nhập kho (WarehouseTransaction)
        warehouseTransferTicketRepository.save(buildAWarehouseTransaction(inStockWh, newItem));

        return saved;
    }

    private InventoryItem buildSparePartItem(CreateInventorySparePartDto req, Warehouse wh, LocalDate orderDate) {
        InventoryItem item = inventoryItemMapper.toInventoryItemSparePart(req);
        item.setWarehouseId(wh.getId());
        item.setInventoryType(InventoryType.SPARE_PART.getId());
        item.setStatus(InventoryItemStatus.IN_STOCK);
        if (item.getLogistics() == null) {
            item.setLogistics(new InventoryItem.Logistics());
        }
        item.getLogistics().setOrderDate(orderDate.atStartOfDay());
        return item;
    }

    private InventoryItem buildProductItem(CreateInventoryProductDto req, Warehouse wh) {
        InventoryItem item = mapper.toInventoryItemModel(req);
        item.setWarehouseId(wh.getId());
        item.setQuantity(1); // Xe hoặc Phụ kiện mặc định là 1
        item.setInitialCondition(true);
        item.setStatus(InventoryItemStatus.IN_STOCK);
        LocalDateTime orderDate = parseOrderDate(req.getLogistics().getOrderDate()).atStartOfDay();
        LocalDateTime estimateCompletionDate = parseOrderDate(req.getLogistics().getEstimateCompletionDate()).atStartOfDay();
        if (item.getLogistics() == null) {
            item.setLogistics(new InventoryItem.Logistics());
        }
        item.getLogistics().setOrderDate(orderDate);
        item.getLogistics().setEstimateCompletionDate(estimateCompletionDate);
        return item;
    }

    private LocalDate parseOrderDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw LogicErrException.of("Ngày phải đúng định dạng 'yyyy-MM-dd'");
        }
    }

    private WarehouseTransaction buildAWarehouseTransaction(Warehouse inStockWh, InventoryItem item) {
        WarehouseTransaction.InventoryItemTicket itemSnapshot = inventoryItemMapper.toInventoryItemTicket(item);

        WarehouseTransaction tran = new WarehouseTransaction();
        tran.setReason("Nhập kho hàng hóa với PO" + item.getPoNumber());
        WarehouseTranType tranType = WarehouseTranType.DATA_ENTRY;
        tran.setTranType(tranType);
        WarehouseSubTranType subTranType = null;
        if (inStockWh.getType().equals(WarehouseType.PRODUCTION)) {
            if (item.getInventoryType().equals(InventoryType.SPARE_PART.getId())) {
                subTranType = WarehouseSubTranType.FORM_TO_PRODUCTION_SPARE_PART;
            } else if (item.getInventoryType().equals(InventoryType.VEHICLE.getId()) || item.getInventoryType().equals(InventoryType.ACCESSORY.getId())) {
                subTranType = WarehouseSubTranType.FORM_TO_PRODUCTION_PRODUCT;
            }

        } else if (inStockWh.getType().equals(WarehouseType.DESTINATION)) {
            if (item.getInventoryType().equals(InventoryType.SPARE_PART.getId())) {
                subTranType = WarehouseSubTranType.FORM_TO_DEST_SPARE_PART;
            } else if (item.getInventoryType().equals(InventoryType.VEHICLE.getId()) || item.getInventoryType().equals(InventoryType.ACCESSORY.getId())) {
                subTranType = WarehouseSubTranType.FORM_TO_DEST_PRODUCT;
            }
        }
        if (subTranType == null) {
            throw LogicErrException.of("Loại kho hoặc loại hàng không được hỗ trợ");
        }
        tran.setSubTranType(subTranType);
        tran.setTitle(tranUtils.generateTranTitle(tranType, subTranType, inStockWh, null));
        // In dept từ wh2
        WarehouseTransaction.Department inDept = new WarehouseTransaction.Department();
        inDept.setName(inStockWh.getName());
        inDept.setAddress(inStockWh.getAddress());
        tran.setStockInDepartment(inDept);

        tran.setStatusEnum(WarehouseTransactionStatus.APPROVED); // Tự động duyệt
        tran.setApprovedAt(LocalDateTime.now());
        tran.setDestinationWarehouseId(item.getWarehouseId());
        tran.setInventoryItems(List.of(itemSnapshot));
        return tran;
    }

    private List<InventoryItem> buildInventoryItemToOtherDestWh(List<InventoryItem> itemsToCreate) {

        List<InventoryItem> dataToSave = new ArrayList<>();
        List<IdProjection> whIds = warehouseRepository.findAllIdsByType(WarehouseType.DESTINATION);
        if (itemsToCreate.isEmpty() || whIds.isEmpty()) {
            return Collections.emptyList();
        }
        for (IdProjection whId : whIds) {
            for (InventoryItem item : itemsToCreate) {
                InventoryItem itemCp = mapper.cloneEntity(item);
                itemCp.setId(null);
                itemCp.setWarehouseId(new ObjectId(whId.getId()));
                dataToSave.add(itemCp);
            }
        }
        return dataToSave;
    }

    private void createImportTransaction(String warehouseId, List<WarehouseTransaction.InventoryItemTicket> dtos, WarehouseSubTranType importType) {
        Warehouse wh = warehouseService.getWarehouseToId(new ObjectId(warehouseId));
        WarehouseTransaction tran = new WarehouseTransaction();
        WarehouseTranType tranType = WarehouseTranType.DATA_ENTRY;
        tran.setTitle(tranUtils.generateTranTitle(tranType, importType, wh, null));
        tran.setInventoryItems(dtos);
        tran.setTranType(tranType);
        tran.setReason("Nhập kho theo lô");
        tran.setSubTranType(importType);
        WarehouseTransaction.Department inDept = new WarehouseTransaction.Department();
        inDept.setName(wh.getName());
        inDept.setAddress(wh.getAddress());
        tran.setStockInDepartment(inDept);
        tran.setStatus(WarehouseTransactionStatus.APPROVED.getId());
        warehouseTransferTicketRepository.save(tran);
    }

    public <T> List<InventoryItem> bulkImport(
            String warehouseId,
            List<T> dtos,
            Function<T, InventoryItem> toInventoryItem,
            Function<T, WarehouseTransaction.InventoryItemTicket> toInventoryItemTicket,
            WarehouseSubTranType importType
    ) {
        if (dtos == null || dtos.isEmpty()) {
            return List.of();
        }

        List<InventoryItem> itemsToInsert = dtos.stream()
                .map(dto -> {
                    InventoryItem item = toInventoryItem.apply(dto);
                    item.setStatus(InventoryItemStatus.IN_STOCK);
                    return item;
                })
                .collect(Collectors.toList());

//        CHỈ CÓ PHỤ TÙNG MỚI TẠO CHO CÁC KHO KHÁC VÌ NÓ QUẢN LÝ THEO SỐ LƯỢNG
        if (importType.equals(WarehouseSubTranType.EXCEL_TO_PRODUCTION_SPARE_PART)) {
            itemsToInsert.addAll(buildInventoryItemToOtherDestWh(itemsToInsert));
        }

        inventoryItemRepository.bulkInsert(itemsToInsert);
        List<WarehouseTransaction.InventoryItemTicket> itemsToTran = dtos.stream()
                .map(toInventoryItemTicket)
                .collect(Collectors.toList());

        createImportTransaction(warehouseId, itemsToTran, importType);

        return itemsToInsert;
    }

    @Transactional
    public List<InventoryItem> bulkCreateProductionProducts(String warehouseId, List<ExcelImportProductionProductDto> dtos) {

        return bulkImport(
                warehouseId,
                dtos,
                mapper::toInventoryItem,
                mapper::toInventoryItemTicket,
                WarehouseSubTranType.EXCEL_TO_PRODUCTION_PRODUCT);
    }

    @Transactional
    public List<InventoryItem> bulkCreateProductionSpareParts(String warehouseId, List<ExcelImportProductionSparePartDto> dtos) {
        return bulkImport(
                warehouseId,
                dtos,
                mapper::toInventoryItem,
                mapper::toInventoryItemTicket,
                WarehouseSubTranType.EXCEL_TO_PRODUCTION_SPARE_PART);
    }


//END   -    NHẬP VÀO KHO BẰNG EXCEL HOẶC FORM

    public InventoryProductDetailsDto getInventoryProductDetails(ObjectId id) {
        InventoryItem item = getItemToId(id);
        return mapper.toInventoryProductDetailsDto(item);
    }

    public InventorySparePartDetailsDto getInventorySparePartDetails(ObjectId id){
        InventoryItem item = getItemToId(id);
        return mapper.toInventorySparePartDetailsDto(item);
    }

    @Transactional
    public InventoryItem updateInventorySparePart(String id, UpdateInventorySparePartDto req){
        InventoryItem item = getItemToId(new ObjectId(id));
        item.setPoNumber(req.getPoNumber());
        item.setCommodityCode(req.getCommodityCode());
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
        return new PageInfoDto<>(itemsPageObject);
    }

    public List<InventoryPoWarehouseDto> getInventoryInStockPoNumbers(List<String> inventoryTypes, String poNumber, String model, String warehouseId, String warehouseType) {
        return inventoryItemRepository.findPoNumbersOfInventoryInStock(warehouseType, inventoryTypes, poNumber, model, warehouseId);
    }

    public List<InventoryItemPoNumberDto> getInventoryInStockByPoNumber(String warehouseType, String warehouseId, String poNumber, String filter){
        return inventoryItemRepository.findInventoryInStockByPoNumber(warehouseType, warehouseId, poNumber, filter);
    }

    @Transactional
    public Warehouse transferItemsProductionToDeparture(InventoryTransferProductionDepartureDto req) {
        Warehouse warehouseDeparture = warehouseService.getWarehouseToId(new ObjectId(req.getDepartureWarehouseId()));
        if(!warehouseDeparture.getType().equals(WarehouseType.DEPARTURE))
            throw LogicErrException.of("Kho cần nhập hàng không phải là kho đi.");
        if(req.getInventoryItems().isEmpty())
            throw LogicErrException.of("Hàng hóa cần nhập sang kho đi hiện đang rỗng.");

        try {
            ObjectId inputItemId = new ObjectId(req.getInventoryItems().get(0).getId());
            Warehouse wh1 = warehouseService.getWarehouseToId(inventoryItemRepository.findWarehouseIdById(inputItemId).get().getWarehouseId());
            Warehouse wh2 = warehouseService.getWarehouseToId(new ObjectId(req.getDepartureWarehouseId()));
            LocalDateTime arrivalDate = LocalDate.parse(req.getArrivalDate()).atStartOfDay();
            List<InventoryItem> items = transferItems(req.getInventoryItems(), warehouseDeparture.getId(), null, arrivalDate, null, InventoryItemStatus.IN_STOCK);
            // TODO: Ghi nhận log chuyển kho (người thực hiện, thời gian, PO, số lượng)
//            BUILD TRAN
            WarehouseTransaction tran = new WarehouseTransaction();
            tran.setInventoryItems(items.stream().map(e -> mapper.toInventoryItemTicket(e)).collect(Collectors.toList()));
            tran.setReason("Nhập hàng từ kho chờ vào kho đi ");
            WarehouseTranType tranType = WarehouseTranType.PRODUCTION_TO_DEPARTURE;
            tran.setTitle(tranUtils.generateTranTitle(tranType, null, wh1, wh2));
            tran.setTranType(tranType);
            tran.setOriginWarehouseId(wh1.getId());
            tran.setDestinationWarehouseId(wh2.getId());

            WarehouseTransaction.Department inDept = new WarehouseTransaction.Department();
            inDept.setName(wh2.getName());
            inDept.setAddress(wh2.getAddress());

            WarehouseTransaction.Department outDept = new WarehouseTransaction.Department();
            outDept.setName(wh1.getName());
            outDept.setAddress(wh1.getAddress());

            tran.setStockInDepartment(inDept);
            tran.setStockOutDepartment(outDept);
            tran.setStatusEnum(WarehouseTransactionStatus.APPROVED); // Tự động duyệt
            tran.setApprovedAt(LocalDateTime.now());
            warehouseTransferTicketRepository.save(tran);
//            - BUILD TRAN
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



    @AuditAction(action = "CREATE_DCNB_TICKET")
    @Transactional
    public Map<String, Object> stockTransfer(InventoryStockTransferDto req) {
        WarehouseTransaction ticket = warehouseTransferTicketService.getWarehouseTransactionToId(new ObjectId(req.getTicketId()));
        Warehouse originWarehouse = warehouseService.getWarehouseToId(new ObjectId(req.getOriginWarehouseId()));
        Warehouse destinationWarehouse = warehouseService.getWarehouseToId(new ObjectId(req.getDestinationWarehouseId()));
        try{
            List<InventoryItem> itemsResults = transferItems(req.getInventoryItems(), destinationWarehouse.getId(), null, null, null, InventoryItemStatus.OTHER);
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
    public InventoryItem updateInventoryProduct(String id, UpdateInventoryProductDto dto){
        InventoryItem inventoryItem = getItemToId(new ObjectId(id));
        inventoryItem.setPoNumber(dto.getPoNumber());
        inventoryItem.setProductCode(dto.getProductCode());
        return inventoryItemRepository.save(inventoryItem);

    }

    @Transactional
    public List<InventoryItem> transferItems(List<InventoryItemTransferDto> items, ObjectId toWarehouseId, Container container, LocalDateTime arrivalDate, LocalDateTime consignmentDate, InventoryItemStatus itemStatus){
        // Hệ thống bắt đầu một giao dịch (transaction)
        Map<ObjectId, Integer> itemIdQualityMap = items.stream().collect(
                Collectors.toMap(e -> new ObjectId(e.getId()), InventoryItemTransferDto::getQuantity)
        );
        // Lấy toàn bộ sản phẩm (theo mã sản phẩm) trong Kho chờ sản xuất có PO được chọn
        List<InventoryItem> itemsToTransfer = inventoryItemRepository.findByIdIn(itemIdQualityMap.keySet());
        List<InventoryItem> itemsSparePartInTransitContainer = List.of();
        if(container != null) {
            List<String> commodityCodes = itemsToTransfer.stream()
                    .filter(i -> i.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && i.getCommodityCode() != null)
                    .map(InventoryItem::getCommodityCode)
                    .toList();
            // Lấy các phụ tùng đang tồn tại trong container (nếu có)
            itemsSparePartInTransitContainer = inventoryItemRepository.findSparePartByCommodityCodeIn(commodityCodes, container.getId());
        }
        List<InventoryItem> itemsSparePartToNew = new ArrayList<>();
        List<InventoryItem> itemsResults = new ArrayList<>();
        for(var item : itemsToTransfer){
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                int quantityToTransfer = itemIdQualityMap.get(item.getId());
                if(quantityToTransfer <= 0) throw LogicErrException.of("Số lượng hàng hóa cần chuyển phải lớn hơn 0.");
                if(item.getQuantity() == 0)
                    throw LogicErrException.of("Hàng phụ tùng " + item.getCommodityCode() + " hiện hết hàng.");
                if(item.getQuantity() < quantityToTransfer)
                    throw LogicErrException.of("Số lượng phụ tùng " + item.getCommodityCode() + " cần nhập vượt quá số lượng trong kho.");
                if(item.getQuantity() > quantityToTransfer){
                    item.setQuantity(item.getQuantity() - quantityToTransfer);
                    InventoryItem sparePartToDeparture = inventoryItemMapper.cloneEntity(item);
                    if(container != null){
                        boolean isExistsInContainer = false;
                        for(var sp : itemsSparePartInTransitContainer){
                            // Nếu phụ tùng có mã hàng hóa tương ứng đã tồn tại trong container thì cập nhật số lượng
                            if(sp.getCommodityCode().equals(item.getCommodityCode())){
                                sp.setQuantity(sp.getQuantity() + quantityToTransfer);
                                isExistsInContainer = true;
                                break;
                            }
                        }
                        // Duyệt qua item tiếp theo
                        if(isExistsInContainer) continue;
                        sparePartToDeparture.setContainerId(container.getId());
                        sparePartToDeparture.getLogistics().setDepartureDate(container.getDepartureDate());
                    }
                    sparePartToDeparture.setId(null);
                    sparePartToDeparture.setQuantity(quantityToTransfer);
                    // Kho hiện tại → “Kho khác”
                    sparePartToDeparture.setWarehouseId(toWarehouseId);
                    sparePartToDeparture.setStatus(itemStatus.getId());
                    if(arrivalDate != null){
                        // Ngày giao hàng = ngày đã chọn theo PO
                        sparePartToDeparture.getLogistics().setArrivalDate(arrivalDate);
                    }
                    if(consignmentDate != null){
                        sparePartToDeparture.getLogistics().setConsignmentDate(consignmentDate);
                    }
                    itemsSparePartToNew.add(sparePartToDeparture);
                    continue;
                }
            }

            if(container != null){
                boolean isExistsInContainer = false;
                for(var sp : itemsSparePartInTransitContainer){
                    // Nếu phụ tùng có mã hàng hóa tương ứng đã tồn tại trong container thì cập nhật số lượng
                    if(sp.getCommodityCode().equals(item.getCommodityCode())){
                        sp.setQuantity(sp.getQuantity() + item.getQuantity());
                        item.setQuantity(0);
                        isExistsInContainer = true;
                        break;
                    }
                }
                // Duyệt qua item tiếp theo
                if(isExistsInContainer) continue;

                item.setContainerId(container.getId());
                item.getLogistics().setDepartureDate(container.getDepartureDate());
            }
            // Kho hiện tại → “Kho khác”
            item.setWarehouseId(toWarehouseId);
            item.setStatus(itemStatus.getId());
            if(arrivalDate != null){
                // Ngày giao hàng = ngày đã chọn theo PO
                item.getLogistics().setArrivalDate(arrivalDate);
            }
            if(consignmentDate != null){
                item.getLogistics().setConsignmentDate(consignmentDate);
            }
            itemsResults.add(item);
        }

        List<ObjectId> itemsQuantityZeroToDel = itemsToTransfer.stream().filter(i -> i.getQuantity() == 0).map(InventoryItem::getId).toList();
        inventoryItemRepository.bulkHardDelete(itemsQuantityZeroToDel);
        inventoryItemRepository.bulkInsert(itemsSparePartToNew);
        inventoryItemRepository.bulkUpdateTransfer(itemsToTransfer);
        inventoryItemRepository.bulkUpdateTransfer(itemsSparePartInTransitContainer);
        itemsResults.addAll(itemsSparePartToNew);
        itemsResults.addAll(itemsSparePartInTransitContainer);
        return itemsResults;
    }


    @AuditAction(action = "testMethod")
    public void approve(Integer approved) {
        if (approved > 0) {
            AuditContext.setDetail("Approved failed because another person has approved");
        } else {
            AuditContext.setDetail("Transaction has been approved by ...");
        }
    }

    public List<InventoryItemModelDto> getAllModels(List<String> inventoryTypes, List<String> warehouseIds, String model){
        List<ObjectId> ids = warehouseIds.stream().map(ObjectId::new).toList();
        return inventoryItemRepository.findAllModelsAndItems(inventoryTypes, ids, model);
    }

    public List<InventoryProductDetailsDto> getProductsByWarehouseId(String warehouseId, String poNumber){
        return inventoryItemRepository.findProductsByWarehouseId(new ObjectId(warehouseId), poNumber);
    }

    public List<InventorySparePartDetailsDto> getSparePartByWarehouseId(String warehouseId, String poNumber){
        return inventoryItemRepository.findSparePartByWarehouseId(new ObjectId(warehouseId), poNumber);
    }
}
