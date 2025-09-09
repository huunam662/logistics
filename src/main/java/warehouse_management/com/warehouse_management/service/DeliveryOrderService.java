package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import warehouse_management.com.warehouse_management.model.Client;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.container.ContainerRepository;
import warehouse_management.com.warehouse_management.repository.delivery_order.DeliveryOrderRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
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
        List<WarehouseForOrder> warehouseForOrders = warehouseRepository.getWarehousesForOrder();
        Map<ObjectId, String> warehouseIdToType = new HashMap<>();
        for (WarehouseForOrder warehouseForOrder : warehouseForOrders) {
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
        return deliveryOrder.getInventoryItems().stream()
                .filter(e -> !e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .map(deliveryOrderMapper::toDeliveryProductDetailsDto)
                .toList();
    }

    public List<DeliverySparePartDetailsDto> getSparePartDetailInDeliveryOrder(ObjectId deliveryOrderId){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(deliveryOrderId);
        if(deliveryOrder.getInventoryItems() == null) return null;
        return deliveryOrder.getInventoryItems().stream()
                .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .map(deliveryOrderMapper::toDeliverySparePartDetailsDto)
                .toList();
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
            if(deliveryOrder.getInventoryItems() == null
                    || deliveryOrder.getInventoryItems().isEmpty()
            || deliveryOrder.getInventoryItems().stream().anyMatch(o -> !o.getIsDelivered())){
                throw LogicErrException.of("Không được phép hoàn tất khi chưa giao đủ.");
            }
        }
        deliveryOrder.setStatus(status.getValue());
        return deliveryOrderRepository.save(deliveryOrder);
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
        Map<ObjectId, InventoryItem> itemsToDeliveryMap = itemsToDelivery.stream().collect(Collectors.toMap(InventoryItem::getId, e -> e));
        List<String> commodityCodes = itemsToDelivery.stream().filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && e.getCommodityCode() != null).map(InventoryItem::getCommodityCode).toList();
        List<InventoryItem> itemsHoldingInWarehouse = inventoryItemRepository.findSparePartByCommodityCodeIn(commodityCodes, InventoryItemStatus.HOLD.getId());
        Map<String, InventoryItem> itemsHoldingInWarehouseMap = itemsHoldingInWarehouse.stream().collect(Collectors.toMap(InventoryItem::getCommodityCode, e -> e));
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

            Warehouse warehouse = warehouseService.getWarehouseToId(item.getWarehouseId());
            if(warehouse.getType() == null) throw LogicErrException.of("Kho "+warehouse.getName()+" không tồn tại loại kho.");

            if(WarehouseType.DEPARTURE.getId().equals(warehouse.getTypeString()) && itemToPush.getIsDelivered()){
                if(!item.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                    throw LogicErrException.of("Sản phẩm "+item.getProductCode()+" thuộc kho "+warehouse.getName()+" không được phép chọn đã giao.");
                else throw LogicErrException.of("Hàng "+item.getCommodityCode()+" thuộc kho "+warehouse.getName()+" không được phép chọn đã giao.");
            }
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                pushSparePartToDeliveryOrderLogic(deliveryOrder, itemToPush, item, sparePartToNew, itemsHoldingInWarehouseMap);
            }
            else{
                if(!itemToPush.getIsDelivered()) item.setStatus(InventoryItemStatus.HOLD.getId());
                else item.setStatus(InventoryItemStatus.SOLD.getId());
                DeliveryOrder.InventoryItemDelivery itemDelivery = inventoryItemMapper.toInventoryItemDelivery(item);
                itemDelivery.setIsDelivered(itemToPush.getIsDelivered());
                deliveryOrder.getInventoryItems().add(itemDelivery);
            }
        }
        inventoryItemRepository.bulkInsert(sparePartToNew);
        List<InventoryItem> itemsToUpdateStatusAndQuantity = new ArrayList<>(Stream.concat(itemsToDelivery.stream(), itemsHoldingInWarehouse.stream()).toList());
        List<ObjectId> sparePartHoldingZero = new ArrayList<>();
        itemsToUpdateStatusAndQuantity = itemsToUpdateStatusAndQuantity.stream().filter(
                o -> {
                    if(o.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && o.getStatus().equals(InventoryItemStatus.HOLD) && o.getQuantity() == 0){
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
            if(item.getQuantity() == 0) item.setStatus(InventoryItemStatus.SOLD.getId());
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
                            if(item.getQuantity() == 0) item.setStatus(InventoryItemStatus.SOLD.getId());
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
                    if(item.getQuantity() == 0) item.setStatus(InventoryItemStatus.SOLD.getId());
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

        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    protected void restoreDeliveryItemsLogic(List<DeliveryOrder.InventoryItemDelivery> itemsInDeliveryList) {

        List<ObjectId> itemsInContIds = new ArrayList<>();

        for(var itemInDelivery : itemsInDeliveryList){

            InventoryItem item = inventoryItemRepository.findById(itemInDelivery.getId()).orElse(null);
            if (item == null) continue;

            if(item.getContainerId() != null){
                itemsInContIds.add(item.getId());
                continue;
            }

            if(!item.getInventoryType().equals(InventoryType.SPARE_PART.getId())) {
                item.setStatus(InventoryItemStatus.IN_STOCK.getId());
                inventoryItemRepository.save(item);
            }
            else{
                if(!itemInDelivery.getIsDelivered()){
                    InventoryItem itemInStock = inventoryItemRepository.findByCommodityCodeAndWarehouseId(itemInDelivery.getCommodityCode(), itemInDelivery.getWarehouseId(), InventoryItemStatus.IN_STOCK.getId()).orElseGet(
                            () -> inventoryItemRepository.findByCommodityCodeAndWarehouseId(itemInDelivery.getCommodityCode(), itemInDelivery.getWarehouseId(), InventoryItemStatus.SOLD.getId())
                                    .orElseThrow(() -> LogicErrException.of("Không tồn tại mặt hàng có sẵn mã '"+itemInDelivery.getCommodityCode()+"' trong kho."))
                    );

                    if(itemInStock == null) itemInStock = inventoryItemMapper.toInventoryItem(itemInDelivery);
                    else itemInStock.setQuantity(itemInStock.getQuantity() + itemInDelivery.getQuantity());

                    itemInStock.setStatus(InventoryItemStatus.IN_STOCK);
                    inventoryItemRepository.save(itemInStock);

                    if(item.getQuantity() <= itemInDelivery.getQuantity()) inventoryItemRepository.deleteById(item.getId());
                    else{
                        item.setQuantity(item.getQuantity() - itemInDelivery.getQuantity());
                        inventoryItemRepository.save(item);
                    }
                }
                else {
                    item.setQuantity(item.getQuantity() + itemInDelivery.getQuantity());
                    if(item.getStatus().equals(InventoryItemStatus.SOLD)) {
                        // Để sang sẵn hãng
                        item.setStatus(InventoryItemStatus.IN_STOCK.getId());
                    }
                    inventoryItemRepository.save(item);
                }
            }
        }
        if(!itemsInContIds.isEmpty()) inventoryItemRepository.updateStatusByIdIn(itemsInContIds, InventoryItemStatus.IN_TRANSIT.getId());
    }

    @Transactional
    public DeliveryOrder updateDeliveryOrderItems(PushItemsDeliveryDto dto){

        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));

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
                    deliveryItem.setIsDelivered(true);
                    InventoryItem product = inventoryInWarehouseMap.get(deliveryItem.getId());
                    product.setStatus(InventoryItemStatus.SOLD.getId());
                    inventoryItemRepository.save(product);
                }
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
        InventoryItem itemInStockOrSold = inventoryItemsOnCommodityCode.stream().filter(e -> e.getStatus().equals(InventoryItemStatus.IN_STOCK) || e.getStatus().equals(InventoryItemStatus.SOLD))
                .findFirst().orElseThrow(() -> LogicErrException.of("Hàng hóa muốn cập nhật hiện không tồn tại trong kho."));
        InventoryItem itemInHolding = inventoryItemsOnCommodityCode.stream().filter(e -> e.getStatus().equals(InventoryItemStatus.HOLD)).findFirst().orElse(null);

        if(!itemToUpdateReq.getIsDelivered()){
            if(deliveryItem.getIsDelivered()){
                pushItemsToDeliveryOrderLogic(List.of(itemToUpdateReq), deliveryOrder);
                return;
            }
            int quantityHoldingCalResult;
            // + Nếu số lượng phụ tùng cập nhật lớn hơn số lượng ban đầu thì kiêm tra số lượng hàng có sẵn (không đáp ứng thì không cho phép cập nhật)
            if(itemToUpdateReq.getQuantity() > deliveryItem.getQuantity()){
                if(itemInStockOrSold == null || itemInStockOrSold.getQuantity() < itemToUpdateReq.getQuantity() - deliveryItem.getQuantity())
                    throw LogicErrException.of("Hàng hóa mã '"+deliveryItem.getCommodityCode()+"' không đủ số lượng sẵn hàng.");
                quantityHoldingCalResult = itemToUpdateReq.getQuantity() - deliveryItem.getQuantity();
                if(itemInHolding != null) itemInHolding.setQuantity(itemInHolding.getQuantity() + quantityHoldingCalResult);
                itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() - quantityHoldingCalResult);
                if(itemInStockOrSold.getQuantity() == 0) itemInStockOrSold.setStatus(InventoryItemStatus.SOLD.getId());
            }
            // + Nếu số lương cập nhật bé hơn số lượng ban đầu thì cập nhật số lượng hàng đang giữ (cộng thêm số lượng bị trừ so với ban đầu)
            else{
                quantityHoldingCalResult = deliveryItem.getQuantity() - itemToUpdateReq.getQuantity();
                if(itemInHolding != null) itemInHolding.setQuantity(itemInHolding.getQuantity() - itemToUpdateReq.getQuantity());
                itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() + quantityHoldingCalResult);
            }
            if(itemInHolding == null){
                itemInHolding = inventoryItemMapper.toInventoryItem(deliveryItem);
                itemInHolding.setId(new ObjectId());
                itemInHolding.setStatus(InventoryItemStatus.HOLD.getId());
                itemInHolding.setQuantity(quantityHoldingCalResult);
            }
            inventoryItemRepository.save(itemInHolding);
            inventoryItemRepository.save(itemInStockOrSold);
            deliveryItem.setQuantity(itemToUpdateReq.getQuantity());
        }
        else{
            // Nếu trong đơn đã tồn tại mặt hàng đã giao nhưng nhận yêu cầu tăng thêm số lượng đã giao
            if(itemInStockOrSold.getId().equals(new ObjectId(itemToUpdateReq.getId()))){
                updatePlusItemsLogic(itemInHolding, itemInStockOrSold, itemToUpdateReq, deliveryItem, deliveryOrder);
                return;
            }
            // - Nếu cập nhật từ trạng thái "chưa giao" sang "đã giao" và cập nhật số lượng thì merge với item "đã giao" (có cùng mã hàng hóa)
            // + Nếu số lượng phụ tùng cập nhật lớn hơn số lượng ban đầu thì kiêm tra số lượng hàng có sẵn (không đáp ứng thì không cho phép cập nhật)
            if(itemToUpdateReq.getQuantity() > deliveryItem.getQuantity()){
                if(itemInStockOrSold.getQuantity() < itemToUpdateReq.getQuantity() - deliveryItem.getQuantity())
                    throw LogicErrException.of("Hàng hóa mã '"+deliveryItem.getCommodityCode()+"' không đủ số lượng trong kho.");

                if(itemInHolding != null){
                    itemInHolding.setQuantity(itemInHolding.getQuantity() - itemToUpdateReq.getQuantity());
                    if(itemInHolding.getQuantity() <= 0) {
                        deliveryOrder.getInventoryItems().remove(deliveryItem);
                        inventoryItemRepository.deleteById(itemInHolding.getId());
                    }
                    else inventoryItemRepository.save(itemInHolding);
                    if(deliveryOrder.getInventoryItems() != null){
                        ObjectId itemInHoldingId = itemInHolding.getId();
                        String commodityCode = itemInHolding.getCommodityCode();
                        DeliveryOrder.InventoryItemDelivery deliveryItemHolding = deliveryOrder.getInventoryItems().stream()
                                .filter(e -> e.getId().equals(itemInHoldingId) && !e.getIsDelivered())
                                .findFirst().orElse(null);
                        if(deliveryItemHolding != null){
                            deliveryItemHolding.setQuantity(deliveryItemHolding.getQuantity() - deliveryItem.getQuantity());
                            if(deliveryItemHolding.getQuantity() == 0) deliveryOrder.getInventoryItems().remove(deliveryItemHolding);
                        }
                        updateItemSoldInDeliveryLogic(itemToUpdateReq, deliveryOrder, itemInStockOrSold, commodityCode);
                    }
                }
            }
            // + Nếu số lương cập nhật bé hơn số lượng ban đầu thì cập nhật số lượng hàng đang giữ (cộng thêm số lượng bị trừ so với ban đầu)
            else{
                if(itemInHolding != null) {
                    itemInHolding.setQuantity(itemInHolding.getQuantity() - itemToUpdateReq.getQuantity());
                    if(itemInHolding.getQuantity() <= 0) {
                        inventoryItemRepository.deleteById(itemInHolding.getId());
                        if(itemInHolding.getQuantity() < 0) {
                            if(itemInStockOrSold.getStatus().equals(InventoryItemStatus.SOLD)
                                    || itemInStockOrSold.getQuantity() < itemInHolding.getQuantity() * -1)
                                throw LogicErrException.of("Hàng hóa mã '"+deliveryItem.getCommodityCode()+"' không đủ số lượng giữ hàng.");
                            itemInStockOrSold.setQuantity(itemInStockOrSold.getQuantity() - (itemInHolding.getQuantity() * -1));
                            inventoryItemRepository.save(itemInStockOrSold);
                        }
                    }
                    else inventoryItemRepository.save(itemInHolding);
                }
                if(deliveryOrder.getInventoryItems() != null){
                    if(itemInHolding != null){
                        ObjectId itemInHoldingId = itemInHolding.getId();
                        String commodityCode = itemInHolding.getCommodityCode();
                        DeliveryOrder.InventoryItemDelivery itemHoldingDeliveryOrder = deliveryOrder.getInventoryItems().stream()
                                .filter(e -> e.getId().equals(itemInHoldingId) && !e.getIsDelivered())
                                .findFirst().orElse(null);
                        if(itemHoldingDeliveryOrder != null){
                            itemHoldingDeliveryOrder.setQuantity(itemHoldingDeliveryOrder.getQuantity() - itemToUpdateReq.getQuantity());
                            if(itemHoldingDeliveryOrder.getQuantity() <= 0) deliveryOrder.getInventoryItems().remove(itemHoldingDeliveryOrder);
                        }
                        updateItemSoldInDeliveryLogic(itemToUpdateReq, deliveryOrder, itemInStockOrSold, commodityCode);
                    }
                }
            }
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
                        deliveryOrder.getInventoryItems().remove(itemHoldingInDelivery);
                    }
                }
                else{
                    itemHoldingInDelivery.setQuantity(itemHoldingInDelivery.getQuantity() - itemToUpdateReq.getQuantity());
                    itemInHolding.setQuantity(itemInHolding.getQuantity() - itemToUpdateReq.getQuantity());
                    inventoryItemRepository.save(itemInHolding);
                }
                if(itemHoldingInDelivery.getQuantity() <= itemInHolding.getQuantity()) {
                    deliveryOrder.getInventoryItems().remove(itemHoldingInDelivery);
                    inventoryItemRepository.deleteById(itemInHoldingId);
                }
            }
            if(notEnoughQuantityInStock) throw LogicErrException.of("Hàng hóa mã '"+deliveryItem.getCommodityCode()+"' không đủ số lượng trong kho.");
        }
        if(itemInStockOrSold.getQuantity() == 0) itemInStockOrSold.setStatus(InventoryItemStatus.SOLD.getId());
        inventoryItemRepository.save(itemInStockOrSold);
        updateItemSoldInDeliveryLogic(itemToUpdateReq, deliveryOrder, itemInStockOrSold, itemInStockOrSold.getCommodityCode());
    }

    private void updateItemSoldInDeliveryLogic(PushItemToDeliveryDto itemToUpdateReq, DeliveryOrder deliveryOrder, InventoryItem itemInStockOrSold, String commodityCode) {
        DeliveryOrder.InventoryItemDelivery itemSoldInDeliveryOrder = deliveryOrder.getInventoryItems().stream()
                .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()) && e.getCommodityCode().equals(commodityCode) && e.getIsDelivered())
                .findFirst().orElse(null);
        if(itemSoldInDeliveryOrder != null) itemSoldInDeliveryOrder.setQuantity(itemSoldInDeliveryOrder.getQuantity() + itemToUpdateReq.getQuantity());
        else {
            DeliveryOrder.InventoryItemDelivery itemSold = inventoryItemMapper.toInventoryItemDelivery(itemInStockOrSold);
            itemSold.setQuantity(itemToUpdateReq.getQuantity());
            itemSold.setIsDelivered(true);
            deliveryOrder.getInventoryItems().add(itemSold);
        }
    }
}