package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.*;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.*;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.DeliveryOrderStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.DeliveryOrderMapper;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.*;
import warehouse_management.com.warehouse_management.dto.warehouse.response.IdAndNameWarehouseDto;
import warehouse_management.com.warehouse_management.repository.delivery_order.DeliveryOrderRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DeliveryOrderService {

    private final DeliveryOrderRepository deliveryOrderRepository;
    private final DeliveryOrderMapper deliveryOrderMapper;
    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemMapper inventoryItemMapper;
    private final WarehouseRepository warehouseRepository;
    private final ClientService clientService;
    private final WarehouseService warehouseService;
    private final CustomAuthentication customAuthentication;

    public DeliveryOrder getDeliveryOrderToId(ObjectId id) {
        return deliveryOrderRepository.findById(id)
                .orElseThrow(() -> LogicErrException.of("Đơn giao hàng không tồn tại."));
    }

    @Transactional
    public DeliveryOrder createDeliveryOrder(CreateDeliveryOrderDto dto) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findByCode(dto.getDeliveryOrderCode()).orElse(null);
        if(deliveryOrder != null) throw LogicErrException.of("Mã đơn " + dto.getDeliveryOrderCode() + " đã tồn tại.");
        //TODO: Tìm khách hàng từ bảng user và gán id vào customerId
        Client client = clientService.getClientToId(new ObjectId(dto.getCustomerId()));
        deliveryOrder = deliveryOrderMapper.toCreateDeliveryOrder(dto);
        deliveryOrder.setCustomerId(client.getId());
        if(dto.getHoldingDays() == null || dto.getHoldingDays() <= 0)
            deliveryOrder.setStatus(DeliveryOrderStatus.UN_DELIVERED.getValue());
        else deliveryOrder.setStatus(DeliveryOrderStatus.HOLD.getValue());
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    public DeliveryOrder updateDeliveryOrder(ObjectId id, UpdateDeliveryOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        if(dto.getStatus().equals(DeliveryOrderStatus.UN_DELIVERED.getValue())
                && deliveryOrder.getInventoryItems() != null && !deliveryOrder.getInventoryItems().isEmpty()){
            throw LogicErrException.of("Đơn hàng hiện đang không có sản phẩm.");
        }
        DeliveryOrder deliveryOrderByCode = deliveryOrderRepository.findByCode(dto.getDeliveryOrderCode()).orElse(null);
        if(deliveryOrderByCode != null && !deliveryOrder.getId().equals(deliveryOrderByCode.getId()))
            throw LogicErrException.of("Mã đơn " + dto.getDeliveryOrderCode() + " đã tồn tại.");
        if(!deliveryOrder.getCustomerId().toString().equals(dto.getCustomerId())){
            //TODO: Kiểm tra sự tồn tại của khách hàng từ bảng user và gán lại customerId nếu cần cập nhật khách hàng liên quan
            Client client = clientService.getClientToId(new ObjectId(dto.getCustomerId()));
            deliveryOrder.setCustomerId(client.getId());
        }
        if(DeliveryOrderStatus.fromValue(dto.getStatus()) == null)
            throw LogicErrException.of("Trạng thái đơn giao hàng không hợp lệ.");
        deliveryOrderMapper.mapToUpdateDeliveryOrder(deliveryOrder, dto);
        return deliveryOrderRepository.save(deliveryOrder);
    }

    public Page<DeliveryOrderPageDto> getPageDeliveryOrder(PageOptionsDto optionsDto){
        return deliveryOrderRepository.findPageDeliveryOrder(optionsDto);
    }

    public DeliveryOrderItemsDto getDeliveryOrderItemTicks(ObjectId id, boolean isSparePart) {
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        List<WarehouseForOrderDto> warehouseForOrders = warehouseRepository.getWarehousesForOrder();
        Map<ObjectId, String> warehouseIdToType = new HashMap<>();
        for (WarehouseForOrderDto warehouseForOrder : warehouseForOrders) {
            warehouseIdToType.put(new ObjectId(warehouseForOrder.getId()), warehouseForOrder.getType());
        }

        DeliveryOrderItemsDto deliveryOrderItemsDto = new DeliveryOrderItemsDto();

        if (deliveryOrder.getInventoryItems() != null) {
            List<DeliveryOrder.InventoryItemDelivery> inventoryItemDeliveries;

            if (isSparePart) {
                inventoryItemDeliveries = deliveryOrder.getInventoryItems().stream()
                        .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                        .toList();
            } else {
                inventoryItemDeliveries = deliveryOrder.getInventoryItems().stream()
                        .filter(e -> !e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                        .toList();
            }
            List<DeliveryItemModelDto> itemsDto = new ArrayList<>();

            for (DeliveryOrder.InventoryItemDelivery inventoryItemDelivery : inventoryItemDeliveries) {
                DeliveryItemModelDto item = deliveryOrderMapper.toDeliveryOrderItemsDto(inventoryItemDelivery);
                item.setWarehouseType(warehouseIdToType.getOrDefault(item.getWarehouseId(),""));
                itemsDto.add(item);
            }

            deliveryOrderItemsDto.setItems(itemsDto);
        }

        if (deliveryOrder.getModelNotes() != null) {
            for (var backDeliveryModel : deliveryOrder.getModelNotes()) {
                if (isSparePart == backDeliveryModel.getIsSparePart()) {
                    deliveryOrderItemsDto.getModelNotes().add(backDeliveryModel);
                }
            }
        }

        return deliveryOrderItemsDto;
    }

    public List<DeliveryProductDetailsDto> getProductDetailInDeliveryOrder(ObjectId deliveryOrderId){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(deliveryOrderId);
        if(deliveryOrder.getInventoryItems() == null) return null;

        List<DeliveryProductDetailsDto> result = new ArrayList<>();

        List<DeliveryProductDetailsDto> deliveryItems =  deliveryOrder.getInventoryItems().stream()
                .filter(e -> !e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .map(deliveryOrderMapper::toDeliveryProductDetailsDto)
                .toList();
        List<DeliveryProductDetailsDto> modelNotes = Optional.ofNullable(deliveryOrder.getModelNotes())
                .orElse(Collections.emptyList())
                .stream()
                .filter(e -> !e.getIsSparePart())
                .map(deliveryOrderMapper::toDeliveryProductDetailsDto)
                .toList();

        result.addAll(deliveryItems);
        result.addAll(modelNotes);
        return result;
    }

    public List<DeliverySparePartDetailsDto> getSparePartDetailInDeliveryOrder(ObjectId deliveryOrderId){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(deliveryOrderId);

        if(deliveryOrder.getInventoryItems() == null) return null;

        List<DeliverySparePartDetailsDto> result = new ArrayList<>();

        List<DeliverySparePartDetailsDto> deliveryItems = deliveryOrder.getInventoryItems().stream()
                .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .map(o -> {
                    DeliverySparePartDetailsDto dto = deliveryOrderMapper.toDeliverySparePartDetailsDto(o);

                    BigDecimal purchasePrice = Optional.ofNullable(o.getPricing())
                            .map(DeliveryOrder.InventoryItemDelivery.Pricing::getPurchasePrice)
                            .orElse(BigDecimal.ZERO);

                    dto.getPricing().setTotalPrice(purchasePrice.multiply(BigDecimal.valueOf(o.getQuantity())));
                    return dto;
                })
                .toList();
        List<DeliverySparePartDetailsDto> noteDeliveryModels = Optional.ofNullable(deliveryOrder.getModelNotes())
                .orElse(Collections.emptyList())
                .stream()
                .filter(note -> note.getIsSparePart().equals(Boolean.TRUE))
                .map(deliveryOrderMapper::toDeliverySparePartNotesDto)
                .toList();

        result.addAll(deliveryItems);
        result.addAll(noteDeliveryModels);

        return result;
    }

    @Transactional
    public DeliveryOrder changeStatusDeliveryOrder(ObjectId id, ChangeStatusDeliveryOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        if(deliveryOrder.getStatus().equals(DeliveryOrderStatus.UN_DELIVERED.getValue()))
            if(deliveryOrder.getInventoryItems() == null || deliveryOrder.getInventoryItems().isEmpty())
                throw LogicErrException.of("Đơn hàng hiện đang không có sản phẩm.");
        if(deliveryOrder.getStatus().equals(DeliveryOrderStatus.REJECTED.getValue()))
            throw LogicErrException.of("Đơn hàng đã được hủy trước đó.");
        if(!dto.getStatus().equals(DeliveryOrderStatus.REJECTED.getValue())
                && deliveryOrder.getStatus().equals(DeliveryOrderStatus.COMPLETED.getValue()))
            throw LogicErrException.of("Đơn hàng đã được hoàn tất trước đó.");
        DeliveryOrderStatus status = DeliveryOrderStatus.fromValue(dto.getStatus());
        if(status == null) throw LogicErrException.of("Trạng thái cần cập nhật không hợp lệ.");
        if(status.equals(DeliveryOrderStatus.REJECTED)){
            if(deliveryOrder.getInventoryItems() != null && !deliveryOrder.getInventoryItems().isEmpty()){
                restoreDeliveryItemsLogic(deliveryOrder.getInventoryItems());
            }
        }
        if(status.equals(DeliveryOrderStatus.COMPLETED)){
            deliveryOrder = completedDeliveryLogic(deliveryOrder);
        }
        deliveryOrder.setStatus(status.getValue());
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    protected DeliveryOrder completedDeliveryLogic(DeliveryOrder deliveryOrder){
        if(deliveryOrder.getInventoryItems() == null || deliveryOrder.getInventoryItems().isEmpty())
            throw LogicErrException.of("Đơn hàng hiện đang không có sản phẩm, hủy thay vì hoàn tất.");

        List<IdAndNameWarehouseDto> warehouseDepartures = warehouseRepository.findIdsByType(WarehouseType.DEPARTURE.getId());

        List<PushItemToDeliveryDto> itemsToDeliveredList = new ArrayList<>();
        for(var item : deliveryOrder.getInventoryItems()){
            warehouseDepartures.stream()
                    .filter(o -> o.getId().equals(item.getWarehouseId()))
                    .findFirst()
                    .ifPresent(o -> {
                        throw LogicErrException.of("Không được phép hoàn tất khi sản phẩm '"+item.getProductCode()+"' vẫn còn ở kho đi ("+o.getName()+").");
                    });
            if(!item.getIsDelivered()){
                PushItemToDeliveryDto itemToDelivered = new PushItemToDeliveryDto();
                itemToDelivered.setId(item.getId().toString());
                itemToDelivered.setQuantity(item.getQuantity());
                itemToDelivered.setIsDelivered(true);
                itemsToDeliveredList.add(itemToDelivered);
            }
        }
        if(!itemsToDeliveredList.isEmpty()){
            PushItemsDeliveryDto updateItemsDelivered = new PushItemsDeliveryDto();
            updateItemsDelivered.setDeliveryOrderId(deliveryOrder.getId().toString());
            updateItemsDelivered.setInventoryItemsDelivery(itemsToDeliveredList);
            deliveryOrder = updateDeliveryOrderItems(updateItemsDelivered);
        }

        return deliveryOrder;
    }

    @Transactional
    public DeliveryOrder addItemsToDeliveryOrder(PushItemsDeliveryDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));
        List<String> statuses = List.of(DeliveryOrderStatus.COMPLETED.getValue(), DeliveryOrderStatus.REJECTED.getValue());

        if(statuses.contains(deliveryOrder.getStatus())) {
            throw LogicErrException.of("Đơn hàng đang ở trong phạm vi không được thêm sản phẩm.");
        }

        List<PushItemToDeliveryDto> itemsToDeliveryDto = dto.getInventoryItemsDelivery();
        if(itemsToDeliveryDto == null) throw LogicErrException.of("Sản phẩm cần thêm vào đơn hàng hiện đang rỗng.");

        pushItemsToDeliveryOrderLogic(itemsToDeliveryDto, deliveryOrder);
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    protected void pushItemsToDeliveryOrderLogic(List<PushItemToDeliveryDto> itemsToDeliveryDto, DeliveryOrder deliveryOrder){

        if(deliveryOrder.getInventoryItems() == null) deliveryOrder.setInventoryItems(new ArrayList<>());

        List<ObjectId> pushItemIds = itemsToDeliveryDto.stream()
                .filter(e -> (e.getId() != null && !e.getId().isBlank()))
                .map(e -> new ObjectId(e.getId())).toList();

        List<InventoryItem> itemsToDelivery = inventoryItemRepository.findByIdIn(pushItemIds);

        Map<ObjectId, InventoryItem> itemsToDeliveryMap = itemsToDelivery.stream()
                .collect(Collectors.toMap(InventoryItem::getId, e -> e));

        List<String> commodityCodes = itemsToDelivery.stream()
                .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId())
                            && e.getCommodityCode() != null)
                .map(InventoryItem::getCommodityCode)
                .toList();

        List<InventoryItem> itemsHoldingInWarehouse = inventoryItemRepository.findSparePartByCommodityCodeIn(commodityCodes, InventoryItemStatus.HOLD.getId());

        Map<String, InventoryItem> itemsHoldingInWarehouseMap = itemsHoldingInWarehouse.stream()
                .collect(Collectors.toMap(InventoryItem::getCommodityCode, e -> e));

        List<InventoryItem> sparePartToNew = new ArrayList<>();

        for(var itemToPush : itemsToDeliveryDto){
            if((itemToPush.getId() == null || itemToPush.getId().isBlank())
            && (itemToPush.getManualModel() == null || itemToPush.getManualModel().isBlank()))
                continue;

            if(itemToPush.getManualModel() != null && !itemToPush.getManualModel().isBlank()
            && (itemToPush.getId() == null || itemToPush.getId().isBlank())){
                if(deliveryOrder.getModelNotes() == null) deliveryOrder.setModelNotes(new ArrayList<>());
                DeliveryOrder.NoteDeliveryModel note = new DeliveryOrder.NoteDeliveryModel();
                note.setModel(itemToPush.getManualModel());
                note.setIsSparePart(itemToPush.getIsSparePart());
                deliveryOrder.getModelNotes().add(note);
                continue;
            }

            if(itemToPush.getQuantity() <= 0) throw LogicErrException.of("Số lượng hàng hóa cần thêm phải lớn hơn 0.");

            InventoryItem item = itemsToDeliveryMap.getOrDefault(new ObjectId(itemToPush.getId()), null);
            if(item == null) throw LogicErrException.of("Mặt hàng cần thêm vào đơn hiện không tồn tại.");

            if(InventoryItemStatus.HOLD.equals(item.getStatus()))
                throw LogicErrException.of("Mặt hàng cần thêm vào đơn hiện không sẵn hàng.");

            Warehouse warehouse = warehouseService.getWarehouseToId(item.getWarehouseId());
            if(warehouse.getType() == null) throw LogicErrException.of("Kho "+warehouse.getName()+" không tồn tại loại kho.");

            if(WarehouseType.DEPARTURE.getId().equals(warehouse.getTypeString()) && itemToPush.getIsDelivered()){
                if(!item.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                    throw LogicErrException.of("Sản phẩm "+item.getProductCode()+" thuộc kho "+warehouse.getName()+" không được phép chọn đã giao.");
                else throw LogicErrException.of("Hàng "+item.getCommodityCode()+" thuộc kho "+warehouse.getName()+" không được phép chọn đã giao.");
            }
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                pushSparePartToDeliveryOrderLogic(deliveryOrder, itemToPush, item, sparePartToNew, itemsHoldingInWarehouseMap);

                addDeliveryHistory(deliveryOrder, "ADD_ITEM", item.getProductCode(), item.getCommodityCode(),
                        item.getModel(), 0, item.getQuantity(),
                        "Chưa có lý do thêm hàng", true);
            }
            else{
                if(!itemToPush.getIsDelivered()) item.setStatus(InventoryItemStatus.HOLD.getId());
                else item.setStatus(InventoryItemStatus.SOLD.getId());
                DeliveryOrder.InventoryItemDelivery itemDelivery = inventoryItemMapper.toInventoryItemDelivery(item);
                itemDelivery.setIsDelivered(itemToPush.getIsDelivered());
                deliveryOrder.getInventoryItems().add(itemDelivery);
                addDeliveryHistory(deliveryOrder, "ADD_ITEM", item.getProductCode(), item.getCommodityCode(),
                        item.getModel(), 0, item.getQuantity(),
                        "Chưa có lý do thêm hàng", false);
            }
        }
        inventoryItemRepository.bulkInsert(sparePartToNew);
        List<InventoryItem> itemsToUpdateStatusAndQuantity = new ArrayList<>(Stream.concat(itemsToDelivery.stream(), itemsHoldingInWarehouse.stream()).toList());
        List<ObjectId> sparePartHoldingZero = new ArrayList<>();
        itemsToUpdateStatusAndQuantity = itemsToUpdateStatusAndQuantity.stream().filter(
                o -> {
                    if(o.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && o.getQuantity() == 0){
                        sparePartHoldingZero.add(o.getId());
                        return false;
                    }
                    return true;
                }
        ).toList();
        inventoryItemRepository.bulkHardDelete(sparePartHoldingZero);
        inventoryItemRepository.bulkUpdateStatusAndQuantity(itemsToUpdateStatusAndQuantity);
    }

    @Transactional
    protected void pushSparePartToDeliveryOrderLogic(DeliveryOrder deliveryOrder, PushItemToDeliveryDto itemToPush, InventoryItem item, List<InventoryItem> sparePartToNew, Map<String, InventoryItem> itemsHoldingInWarehouseMap){

        int quantityToDelivery = itemToPush.getQuantity();

        if(quantityToDelivery == 0) throw LogicErrException.of("Phụ tùng mã "+item.getCommodityCode()+" số lượng muốn thêm phái lớn hơn 0" );

        if(item.getQuantity() == 0)
            throw LogicErrException.of("Hàng phụ tùng " + item.getCommodityCode() + " hiện hết hàng.");

        if(!itemToPush.getIsDelivered()) {

            if(item.getQuantity() < quantityToDelivery)
                throw LogicErrException.of("Số lượng phụ tùng '" + item.getCommodityCode() + "' cần giữ hàng vượt quá số lượng trong kho.");

            item.setQuantity(item.getQuantity() - quantityToDelivery);
            InventoryItem itemHolding = itemsHoldingInWarehouseMap.getOrDefault(item.getCommodityCode(), null);

            if(itemHolding == null){

                InventoryItem sparePartClone = inventoryItemMapper.cloneEntity(item);
                sparePartClone.setId(new ObjectId());
                sparePartClone.setStatus(InventoryItemStatus.HOLD.getId());
                sparePartClone.setQuantity(quantityToDelivery);
                sparePartToNew.add(sparePartClone);

                DeliveryOrder.InventoryItemDelivery itemDelivery = inventoryItemMapper.toInventoryItemDelivery(sparePartClone);
                itemDelivery.setIsDelivered(itemToPush.getIsDelivered());
                deliveryOrder.getInventoryItems().add(itemDelivery);
            }
            else {
                itemHolding.setQuantity(itemHolding.getQuantity() + quantityToDelivery);

                boolean isExistsSparePartHolding = false;

                for(var i : deliveryOrder.getInventoryItems()){
                    if(i.getId().equals(itemHolding.getId())){
                        i.setQuantity(i.getQuantity() + quantityToDelivery);
                        isExistsSparePartHolding = true;
                        break;
                    }
                }

                if(!isExistsSparePartHolding){
                    DeliveryOrder.InventoryItemDelivery itemDelivery = inventoryItemMapper.toInventoryItemDelivery(itemHolding);
                    itemDelivery.setQuantity(itemToPush.getQuantity());
                    itemDelivery.setIsDelivered(itemToPush.getIsDelivered());
                    deliveryOrder.getInventoryItems().add(itemDelivery);
                }
            }
        }
        else{
            Optional<DeliveryOrder.InventoryItemDelivery> sparePartInOrderOp = deliveryOrder.getInventoryItems().stream().filter(
                    s -> s.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && s.getCommodityCode().equals(item.getCommodityCode()) && s.getIsDelivered()
            ).findFirst();

            if(sparePartInOrderOp.isEmpty()){
                DeliveryOrder.InventoryItemDelivery sparePartDelivery = inventoryItemMapper.toInventoryItemDelivery(item);
                sparePartDelivery.setQuantity(quantityToDelivery);
                sparePartDelivery.setIsDelivered(itemToPush.getIsDelivered());
                deliveryOrder.getInventoryItems().add(sparePartDelivery);
            }
            else{
                DeliveryOrder.InventoryItemDelivery sparePartInOrder = sparePartInOrderOp.get();
                sparePartInOrder.setQuantity(sparePartInOrder.getQuantity() + quantityToDelivery);
            }

            DeliveryOrder.InventoryItemDelivery sparePartHoldingInOrder = deliveryOrder.getInventoryItems()
                    .stream().filter(o -> o.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && o.getCommodityCode().equals(item.getCommodityCode()) && !o.getIsDelivered())
                    .findFirst().orElse(null);

            if(sparePartHoldingInOrder != null){

                sparePartHoldingInOrder.setQuantity(sparePartHoldingInOrder.getQuantity() - quantityToDelivery);

                if(sparePartHoldingInOrder.getQuantity() == 0) deliveryOrder.getInventoryItems().remove(sparePartHoldingInOrder);

                InventoryItem itemHolding = itemsHoldingInWarehouseMap.getOrDefault(item.getCommodityCode(), null);
                if(itemHolding != null) {
                    if(itemHolding.getQuantity() < quantityToDelivery){
                        if(item.getQuantity() < quantityToDelivery)
                            throw LogicErrException.of("Số lượng phụ tùng " + item.getCommodityCode() + " cần giao vượt quá số lượng trong kho.");
                        else{
                            item.setQuantity(item.getQuantity() - (quantityToDelivery - itemHolding.getQuantity()));
                        }
                    }
                    itemHolding.setQuantity(itemHolding.getQuantity() - quantityToDelivery);
                }
            }
            else {
                if(item.getQuantity() < quantityToDelivery)
                    throw LogicErrException.of("Số lượng phụ tùng " + item.getCommodityCode() + " cần giao vượt quá số lượng trong kho.");
                else {
                    item.setQuantity(item.getQuantity() - quantityToDelivery);
                }
            }
        }
    }

    @Transactional
    public DeliveryOrder removeItem(DeleteItemsOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));

        if(deliveryOrder.getInventoryItems() == null || deliveryOrder.getInventoryItems().isEmpty())
            throw LogicErrException.of("Đơn hàng hiện không có mặt hàng nào");

        List<ObjectId> deliveryOrderIds = dto.getItemIds().stream().map(ObjectId::new).toList();

        List<DeliveryOrder.InventoryItemDelivery> items = deliveryOrder.getInventoryItems()
                .stream().filter(o -> deliveryOrderIds.contains(o.getId())).toList();

        if(items.isEmpty()) throw LogicErrException.of("Hàng hóa trong đơn không tồn tại.");

        restoreDeliveryItemsLogic(items);

        deliveryOrder.getInventoryItems().removeAll(items);

        // Track history before removing items
        for (DeliveryOrder.InventoryItemDelivery item : items) {
            addDeliveryHistory(
                    deliveryOrder,
                    "REMOVE_ITEM",
                    item.getProductCode(),
                    item.getCommodityCode(),
                    item.getModel(),
                    item.getQuantity(),
                    0,
                    "Chưa có lý do xóa hàng",
                    item.getInventoryType().equals(InventoryType.SPARE_PART.getId())
            );
        }
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    protected void restoreDeliveryItemsLogic(List<DeliveryOrder.InventoryItemDelivery> itemsInDeliveryList) {

        List<ObjectId> itemsInContIds = new ArrayList<>();

        for(var itemInDelivery : itemsInDeliveryList){

            if(!itemInDelivery.getInventoryType().equals(InventoryType.SPARE_PART.getId())) {
                InventoryItem vehicle = inventoryItemRepository.findById(itemInDelivery.getId())
                        .orElseThrow(() -> LogicErrException.of("Sản phẩm '" + itemInDelivery.getProductCode() + "' không tồn tại."));
                if(vehicle.getContainerId() != null)
                    itemsInContIds.add(vehicle.getContainerId());
                else {
                    vehicle.setStatus(InventoryItemStatus.IN_STOCK.getId());
                    inventoryItemRepository.save(vehicle);
                }
            }
            else{
                InventoryItem sparePartInStock = inventoryItemRepository.findByCommodityCodeAndWarehouseId(itemInDelivery.getCommodityCode(), itemInDelivery.getWarehouseId(), InventoryItemStatus.IN_STOCK.getId()).orElse(null);

                if(sparePartInStock == null){
                    sparePartInStock = inventoryItemMapper.toInventoryItem(itemInDelivery);
                    sparePartInStock.setStatus(InventoryItemStatus.IN_STOCK);
                    sparePartInStock.setQuantity(itemInDelivery.getQuantity());
                }
                else sparePartInStock.setQuantity(sparePartInStock.getQuantity() + itemInDelivery.getQuantity());

                inventoryItemRepository.save(sparePartInStock);

                if(!itemInDelivery.getIsDelivered()){

                    InventoryItem sparePartHolding = inventoryItemRepository.findById(itemInDelivery.getId())
                            .orElseThrow(() -> LogicErrException.of("Phụ tùng '" + itemInDelivery.getCommodityCode() + "' không tồn tại giữ hàng."));

                    sparePartHolding.setQuantity(sparePartHolding.getQuantity() - itemInDelivery.getQuantity());

                    if(sparePartHolding.getQuantity() == 0) inventoryItemRepository.deleteById(sparePartHolding.getId());
                    else inventoryItemRepository.save(sparePartHolding);
                }
            }
        }
        if(!itemsInContIds.isEmpty()) inventoryItemRepository.updateStatusByIdIn(itemsInContIds, InventoryItemStatus.IN_TRANSIT.getId());
    }

    @Transactional
    public DeliveryOrder updateDeliveryOrderItems(PushItemsDeliveryDto dto){

        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));

        List<String> statuses = List.of(DeliveryOrderStatus.COMPLETED.getValue(), DeliveryOrderStatus.REJECTED.getValue());

        if(statuses.contains(deliveryOrder.getStatus())) {
            throw LogicErrException.of("Đơn hàng đang ở trong phạm vi không được cập nhật sản phẩm.");
        }

        if(dto.getInventoryItemsDelivery() == null || dto.getInventoryItemsDelivery().isEmpty())
            return deliveryOrder;

        for(var itemDelivery : deliveryOrder.getInventoryItems()){

            dto.getInventoryItemsDelivery()
                    .stream().filter(o -> o.getId() != null
                            && !o.getId().isBlank()
                            && (o.getManualModel() == null || o.getManualModel().isBlank())
                            && itemDelivery.getId().equals(new ObjectId(o.getId()))
                            && itemDelivery.getQuantity().equals(o.getQuantity())
                            && itemDelivery.getIsDelivered().equals(o.getIsDelivered()))
                    .findFirst()
                    .ifPresent(o -> dto.getInventoryItemsDelivery().remove(o));
        }
        Map<ObjectId, DeliveryOrder.InventoryItemDelivery> deliveryOrderMap = deliveryOrder.getInventoryItems().stream()
                .collect(Collectors.toMap(DeliveryOrder.InventoryItemDelivery::getId, e -> e));

        List<PushItemToDeliveryDto> itemsReqToPushNew = new ArrayList<>();

        List<PushItemToDeliveryDto> itemsReqToPushUpdate = new ArrayList<>();

        for(var itemReq : dto.getInventoryItemsDelivery()){
            if((itemReq.getId() == null || itemReq.getId().isBlank())
                    && (itemReq.getManualModel() == null || itemReq.getManualModel().isBlank()))
                continue;

            if(itemReq.getId() != null && !itemReq.getId().isBlank()
                && deliveryOrderMap.containsKey(new ObjectId(itemReq.getId()))) {
                itemsReqToPushUpdate.add(itemReq);
            }
            else {
                itemsReqToPushNew.add(itemReq);
            }
            // Handle remove manual model if it has updated to new existing item
            if(itemReq.getManualModel() != null && !itemReq.getManualModel().isBlank()) {
                if(deliveryOrder.getModelNotes() != null){
                    deliveryOrder.getModelNotes()
                            .removeIf(note -> note.getModel().equals(itemReq.getManualModel()));
                }
            }
        }
        // - Nếu item không tồn tại thì thêm mới
        if(!itemsReqToPushNew.isEmpty()) pushItemsToDeliveryOrderLogic(itemsReqToPushNew, deliveryOrder);

        Map<ObjectId, InventoryItem> inventoryInWarehouseMap = inventoryItemRepository.findByIdIn(deliveryOrderMap.keySet()).stream()
                .collect(Collectors.toMap(InventoryItem::getId, e -> e));

        for(var itemToUpdateReq : itemsReqToPushUpdate){
            DeliveryOrder.InventoryItemDelivery deliveryItem = deliveryOrderMap.get(new ObjectId(itemToUpdateReq.getId()));

            Warehouse warehouse = warehouseService.getWarehouseToId(deliveryItem.getWarehouseId());
            if(warehouse.getType() == null) throw LogicErrException.of("Kho "+warehouse.getName()+" không tồn tại loại kho.");

            if(WarehouseType.DEPARTURE.getId().equals(warehouse.getTypeString()) && itemToUpdateReq.getIsDelivered()){

                if(!deliveryItem.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                    throw LogicErrException.of("Sản phẩm "+deliveryItem.getProductCode()+" thuộc kho "+warehouse.getName()+" không được phép chọn đã giao.");
                else throw LogicErrException.of("Hàng "+deliveryItem.getCommodityCode()+" thuộc kho "+warehouse.getName()+" không được phép chọn đã giao.");
            }
            // - Nếu là xe & phụ kiện
            if (!deliveryItem.getInventoryType().equals(InventoryType.SPARE_PART.getId())) {

                if (itemToUpdateReq.getQuantity() != 1)
                    throw LogicErrException.of("Số lượng sản phẩm mã '" + deliveryItem.getProductCode() + "' mặc định luôn là 1.");
                // - Đối với sản phẩm xe & phụ kiện thì chỉ xóa hoặc thêm mới hoặc cho phép đổi trạng thái "chưa giao" sang "đã giao"
                if (itemToUpdateReq.getIsDelivered()) {

                    if(deliveryItem.getLogistics() == null) deliveryItem.setLogistics(new DeliveryOrder.InventoryItemDelivery.Logistics());

                    deliveryItem.setIsDelivered(true);
                    deliveryItem.getLogistics().setArrivalDate(LocalDateTime.now());
                    InventoryItem product = inventoryInWarehouseMap.get(deliveryItem.getId());
                    product.setStatus(InventoryItemStatus.SOLD.getId());
                    inventoryItemRepository.save(product);
                }
                addDeliveryHistory(
                        deliveryOrder,
                        "UPDATE_ITEM",
                        deliveryItem.getProductCode(),
                        deliveryItem.getCommodityCode(),
                        deliveryItem.getModel(),
                        deliveryItem.getQuantity(),
                        itemToUpdateReq.getQuantity(),
                        "Chưa có lý do cập nhật hàng",
                        false
                );
            }
            // - Nếu là phụ tùng
            // */   + Có 2 trường hợp
            //      ++ 1. chưa giao -> chưa giao, TH chỉ cập nhật số lượng (cập nhật số lượng giu hàng nếu hàng có sẵn đáp ứng)
            //      ++ 2. chưa giao -> đã giao, TH có thể cập nhật số lượng (cập nhật số lượng giao nếu hàng có sẵn đáp ứng)
            // */
            else sparePartUpdateLogic(itemToUpdateReq, deliveryItem, deliveryOrder);
        }
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    protected void sparePartUpdateLogic(PushItemToDeliveryDto itemToUpdateReq, DeliveryOrder.InventoryItemDelivery deliveryItem, DeliveryOrder deliveryOrder){

        if(itemToUpdateReq.getQuantity() == 0) throw LogicErrException.of("Phụ tùng mã '"+deliveryItem.getCommodityCode()+"' số lượng muốn cập nhật phải lớn hơn 0" );

        List<InventoryItem> inventoryItemsOnCommodityCode = inventoryItemRepository.findByCommodityCodeAndWarehouseIdList(deliveryItem.getCommodityCode(), deliveryItem.getWarehouseId());

        InventoryItem itemInStockOrSold = inventoryItemsOnCommodityCode.stream()
                .filter(e -> e.getStatus().equals(InventoryItemStatus.IN_STOCK))
                .findFirst().orElse(null);

        InventoryItem itemInHolding = inventoryItemsOnCommodityCode.stream()
                .filter(e -> e.getStatus().equals(InventoryItemStatus.HOLD))
                .findFirst().orElse(null);

        if(!itemToUpdateReq.getIsDelivered()){

            if(deliveryItem.getIsDelivered()){

                pushItemsToDeliveryOrderLogic(List.of(itemToUpdateReq), deliveryOrder);
                return;
            }
            if(itemInStockOrSold == null || itemInStockOrSold.getQuantity() < itemToUpdateReq.getQuantity())
                throw LogicErrException.of("Phụ tùng '" + deliveryItem.getCommodityCode() + "' không đủ số lượng sẵn hàng.");

            if(itemInHolding == null){

                itemInHolding = inventoryItemMapper.toInventoryItem(deliveryItem);
                itemInHolding.setId(new ObjectId());
                itemInHolding.setStatus(InventoryItemStatus.HOLD.getId());
                itemInHolding.setQuantity(itemToUpdateReq.getQuantity());
            }
            else itemInHolding.setQuantity(itemInHolding.getQuantity() + itemToUpdateReq.getQuantity());

            itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() - itemToUpdateReq.getQuantity());

            if(itemInStockOrSold.getQuantity() == 0) inventoryItemRepository.deleteById(itemInStockOrSold.getId());
            else inventoryItemRepository.save(itemInStockOrSold);

            inventoryItemRepository.save(itemInHolding);

            deliveryItem.setQuantity(deliveryItem.getQuantity() + itemToUpdateReq.getQuantity());
        }
        else{
            // Nếu trong đơn đã tồn tại mặt hàng đã giao nhưng nhận yêu cầu tăng thêm số lượng đã giao
            if(itemInStockOrSold != null && itemInStockOrSold.getId().equals(new ObjectId(itemToUpdateReq.getId()))){
                updatePlusItemsLogic(itemInHolding, itemInStockOrSold, itemToUpdateReq, deliveryItem, deliveryOrder);
                return;
            }
            // - Nếu cập nhật từ trạng thái "chưa giao" sang "đã giao" và cập nhật số lượng thì merge với item "đã giao" (có cùng mã hàng hóa)
            if(itemInHolding == null) throw LogicErrException.of("Phụ tùng '" + deliveryItem.getCommodityCode() + "' hiện không tôn tại giữ hàng trong kho.");

            itemInHolding.setQuantity(itemInHolding.getQuantity() - itemToUpdateReq.getQuantity());

            if(itemInHolding.getQuantity() == 0) inventoryItemRepository.deleteById(itemInHolding.getId());
            else inventoryItemRepository.save(itemInHolding);

            InventoryItem itemToClone = itemInStockOrSold != null ? itemInStockOrSold : itemInHolding;

            updateItemSoldInDeliveryLogic(itemToUpdateReq, deliveryOrder, itemToClone, itemInHolding.getCommodityCode());

            deliveryOrder.getInventoryItems().remove(deliveryItem);
        }
    }

    @Transactional
    protected void updatePlusItemsLogic(
            InventoryItem itemInHolding,
            InventoryItem itemInStockOrSold,
            PushItemToDeliveryDto itemToUpdateReq,
            DeliveryOrder.InventoryItemDelivery deliveryItem,
            DeliveryOrder deliveryOrder
    ){
        if(itemInHolding == null){

            if(itemInStockOrSold.getQuantity() < itemToUpdateReq.getQuantity())
                throw LogicErrException.of("Hàng hóa mã '"+deliveryItem.getCommodityCode()+"' không đủ số lượng trong kho.");

            itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() - itemToUpdateReq.getQuantity());
        }
        else {

            ObjectId itemInHoldingId = itemInHolding.getId();

            DeliveryOrder.InventoryItemDelivery itemHoldingInDelivery = deliveryOrder.getInventoryItems()
                    .stream().filter(o -> o.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && o.getId().equals(itemInHoldingId))
                    .findFirst().orElse(null);

            boolean notEnoughQuantityInStock = false;

            if(itemHoldingInDelivery == null){

                notEnoughQuantityInStock = itemInStockOrSold.getQuantity() < itemToUpdateReq.getQuantity();

                if(!notEnoughQuantityInStock) itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() - itemToUpdateReq.getQuantity());
            }
            else{
                if(itemHoldingInDelivery.getQuantity() < itemToUpdateReq.getQuantity()){

                    notEnoughQuantityInStock = itemInStockOrSold.getQuantity() + itemHoldingInDelivery.getQuantity() < itemToUpdateReq.getQuantity();

                    if(!notEnoughQuantityInStock) {

                        itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() + itemHoldingInDelivery.getQuantity() - itemToUpdateReq.getQuantity());
                        itemInHolding.setQuantity(itemInHolding.getQuantity() - itemHoldingInDelivery.getQuantity());
                        inventoryItemRepository.save(itemInHolding);
                        deliveryOrder.getInventoryItems().remove(itemHoldingInDelivery);
                    }
                }
                else{

                    itemHoldingInDelivery.setQuantity(itemHoldingInDelivery.getQuantity() - itemToUpdateReq.getQuantity());

                    if(itemHoldingInDelivery.getQuantity() == 0) deliveryOrder.getInventoryItems().remove(itemHoldingInDelivery);

                    itemInHolding.setQuantity(itemInHolding.getQuantity() - itemToUpdateReq.getQuantity());

                    if(itemInHolding.getQuantity() == 0) inventoryItemRepository.deleteById(itemInHolding.getId());
                    else inventoryItemRepository.save(itemInHolding);
                }
            }
            if(notEnoughQuantityInStock) throw LogicErrException.of("Hàng hóa mã '"+deliveryItem.getCommodityCode()+"' không đủ số lượng trong kho.");
        }

        if(itemInStockOrSold.getQuantity() == 0) inventoryItemRepository.deleteById(itemInStockOrSold.getId());
        else inventoryItemRepository.save(itemInStockOrSold);

        updateItemSoldInDeliveryLogic(itemToUpdateReq, deliveryOrder, itemInStockOrSold, itemInStockOrSold.getCommodityCode());
    }

    private void updateItemSoldInDeliveryLogic(PushItemToDeliveryDto itemToUpdateReq, DeliveryOrder deliveryOrder, InventoryItem sparePartToClone, String commodityCode) {
        DeliveryOrder.InventoryItemDelivery itemSoldInDeliveryOrder = deliveryOrder.getInventoryItems().stream()
                .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && e.getCommodityCode().equals(commodityCode) && e.getIsDelivered())
                .findFirst().orElse(null);

        if(itemSoldInDeliveryOrder != null) itemSoldInDeliveryOrder.setQuantity(itemSoldInDeliveryOrder.getQuantity() + itemToUpdateReq.getQuantity());
        else {
            DeliveryOrder.InventoryItemDelivery itemSold = inventoryItemMapper.toInventoryItemDelivery(sparePartToClone);
            itemSold.setQuantity(itemToUpdateReq.getQuantity());
            itemSold.setIsDelivered(true);
            deliveryOrder.getInventoryItems().add(itemSold);
        }
    }

    public List<DeliveryOrder.DeliveryHistory> getDeliveryHistory(ObjectId deliveryOrderId) {
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(deliveryOrderId);
        if (deliveryOrder.getDeliveryHistories() == null) {
            return new ArrayList<>();
        }

        return deliveryOrder.getDeliveryHistories().stream()
                .sorted((h1, h2) -> h2.getPerformedAt().compareTo(h1.getPerformedAt()))
                .collect(Collectors.toList());
    }

    private void addDeliveryHistory(DeliveryOrder deliveryOrder, String action, String productCode,
                                    String commodityCode, String model, Integer originalQuantity,
                                    Integer newQuantity, String reason, Boolean isSparePart) {
        if (deliveryOrder.getDeliveryHistories() == null) {
            deliveryOrder.setDeliveryHistories(new ArrayList<>());
        }

        DeliveryOrder.DeliveryHistory history = new DeliveryOrder.DeliveryHistory();
        history.setId(new ObjectId());
        history.setAction(action);
        history.setProductCode(productCode);
        history.setCommodityCode(commodityCode);
        history.setModel(model);
        history.setOriginalQuantity(originalQuantity);
        history.setNewQuantity(newQuantity);
        history.setReason(reason);
        history.setPerformedBy(customAuthentication.getUser().getFullName());
        history.setPerformedAt(LocalDateTime.now());
        history.setIsSparePart(isSparePart);

        deliveryOrder.getDeliveryHistories().add(history);
    }
}