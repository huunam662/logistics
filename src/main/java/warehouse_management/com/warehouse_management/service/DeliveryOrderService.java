package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.*;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemModelDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.enumerate.DeliveryOrderStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.InventoryType;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.DeliveryOrderMapper;
import warehouse_management.com.warehouse_management.mapper.InventoryItemMapper;
import warehouse_management.com.warehouse_management.model.Client;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.repository.delivery_order.DeliveryOrderRepository;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;
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
    private final ClientService clientService;

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
        deliveryOrder.setStatus(DeliveryOrderStatus.UN_DELIVERED.getValue());
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

    public DeliveryOrderItemsDto getDeliveryOrderItemTicks(ObjectId id, boolean isSparePart){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        DeliveryOrderItemsDto deliveryOrderItemsDto = new DeliveryOrderItemsDto();
        if(deliveryOrder.getInventoryItems() != null){
            List<DeliveryOrder.InventoryItemDelivery> inventoryItemDeliveries = isSparePart
                    ? deliveryOrder.getInventoryItems().stream().filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId())).toList()
                    : deliveryOrder.getInventoryItems().stream().filter(e -> !e.getInventoryType().equals(InventoryType.SPARE_PART.getId())).toList();
            List<DeliveryItemModelDto> itemsDto = inventoryItemDeliveries.stream().map(deliveryOrderMapper::toDeliveryOrderItemsDto).toList();
            deliveryOrderItemsDto.setItems(itemsDto);
        }
        if(deliveryOrder.getModelNotes() != null){
            for(var backDeliveryModel : deliveryOrder.getModelNotes()){
                if(isSparePart == backDeliveryModel.getIsSparePart()){
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
                .sorted(Comparator.comparing(DeliveryOrder.InventoryItemDelivery::getIsDelivered, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(deliveryOrderMapper::toDeliveryProductDetailsDto)
                .toList();
    }

    public List<DeliverySparePartDetailsDto> getSparePartDetailInDeliveryOrder(ObjectId deliveryOrderId){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(deliveryOrderId);
        if(deliveryOrder.getInventoryItems() == null) return null;
        return deliveryOrder.getInventoryItems().stream()
                .filter(e -> e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .sorted(Comparator.comparing(DeliveryOrder.InventoryItemDelivery::getIsDelivered, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(deliveryOrderMapper::toDeliverySparePartDetailsDto)
                .toList();
    }

    @Transactional
    public DeliveryOrder addNotesToDeliveryOrder(PushNotesOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));
        List<String> statuses = List.of(DeliveryOrderStatus.COMPLETED.getValue(), DeliveryOrderStatus.REJECTED.getValue());
        if(statuses.contains(deliveryOrder.getStatus()))
            throw LogicErrException.of("Đơn hàng đang ở trong phạm vi không được thêm ghi nợ.");
        List<DeliveryOrder.BackDeliveryModel> notesToDeliveryDto = dto.getNotes();
        if(notesToDeliveryDto == null) throw LogicErrException.of("Ghi chú cần thêm vào đơn hàng hiện đang rỗng.");
        if(deliveryOrder.getModelNotes() == null) deliveryOrder.setModelNotes(new ArrayList<>());
        deliveryOrder.getModelNotes().addAll(notesToDeliveryDto);
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    public DeliveryOrder removeNotesInDeliveryOrder(DeleteNotesOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));
        if(deliveryOrder.getInventoryItems() == null || deliveryOrder.getInventoryItems().isEmpty())
            throw LogicErrException.of("Đơn hàng hiện không có ghi nợ nào");
        for(var modelNote : dto.getModels()){
            DeliveryOrder.BackDeliveryModel m = deliveryOrder.getModelNotes().stream().filter(e -> e.getModel().equals(modelNote)).findFirst().orElse(null);
            if(m == null) throw LogicErrException.of("Nội dung '" + modelNote + "' hiện không có trong danh sách ghi nợ.");
            deliveryOrder.getModelNotes().remove(m);
        }
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    public DeliveryOrder changeStatusDeliveryOrder(ObjectId id, ChangeStatusDeliveryOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        if(dto.getStatus().equals(DeliveryOrderStatus.UN_DELIVERED.getValue())
                && deliveryOrder.getInventoryItems() != null && !deliveryOrder.getInventoryItems().isEmpty()){
            throw LogicErrException.of("Đơn hàng hiện đang không có sản phẩm.");
        }
        DeliveryOrderStatus status = DeliveryOrderStatus.fromValue(dto.getStatus());
        if(status.equals(DeliveryOrderStatus.REJECTED)){
            
        }
        deliveryOrder.setStatus(status.getValue());
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    public DeliveryOrder addItemsToDeliveryOrder(PushItemsDeliveryDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));
        List<String> statuses = List.of(DeliveryOrderStatus.COMPLETED.getValue(), DeliveryOrderStatus.REJECTED.getValue());
        if(statuses.contains(deliveryOrder.getStatus()))
            throw LogicErrException.of("Đơn hàng đang ở trong phạm vi không được thêm sản phẩm.");
        List<PushItemToDeliveryDto> itemsToDeliveryDto = dto.getInventoryItemsDelivery();
        if(itemsToDeliveryDto == null) throw LogicErrException.of("Sản phẩm cần thêm vào đơn hàng hiện đang rỗng.");
        pushItemsToDeliveryOrderLogic(itemsToDeliveryDto, deliveryOrder);
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    public void pushItemsToDeliveryOrderLogic(List<PushItemToDeliveryDto> itemsToDeliveryDto, DeliveryOrder deliveryOrder){
        if(deliveryOrder.getInventoryItems() == null) deliveryOrder.setInventoryItems(new ArrayList<>());
        List<ObjectId> pushItemIds = itemsToDeliveryDto.stream().filter(e -> e.getId() != null).map(e -> new ObjectId(e.getId())).toList();
        List<InventoryItem> itemsToDelivery = inventoryItemRepository.findByIdIn(pushItemIds);
        Map<ObjectId, InventoryItem> itemsToDeliveryMap = itemsToDelivery.stream().collect(Collectors.toMap(InventoryItem::getId, e -> e));
        List<InventoryItem> itemsHoldingInWarehouse = inventoryItemRepository.findSparePartByCommodityCodeIn(pushItemIds, InventoryItemStatus.HOLD.getId());
        Map<ObjectId, InventoryItem> itemsHoldingInWarehouseMap = itemsHoldingInWarehouse.stream().collect(Collectors.toMap(InventoryItem::getId, e -> e));
        List<InventoryItem> sparePartToNew = new ArrayList<>();
        for(var itemToPush : itemsToDeliveryDto){
            if(itemToPush.getQuantity() <= 0) throw LogicErrException.of("Số lượng hàng hóa cần thêm phải lớn hơn 0.");
            InventoryItem item = itemsToDeliveryMap.getOrDefault(new ObjectId(itemToPush.getId()), null);
            if(item == null) throw LogicErrException.of("Mặt hàng cần thêm vào đơn hiện không tồn tại.");
            if(item.getContainerId() != null && itemToPush.getIsDelivered())
                throw LogicErrException.of("Hàng "+item.getCommodityCode()+" hiện đang đi đường nên không cho phép chọn đã giao.");
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                int quantityToDelivery = itemToPush.getQuantity();
                if(item.getQuantity() == 0)
                    throw LogicErrException.of("Hàng phụ tùng " + item.getCommodityCode() + " hiện hết hàng.");
                if(item.getQuantity() < quantityToDelivery)
                    throw LogicErrException.of("Số lượng phụ tùng " + item.getCommodityCode() + " cần giao vượt quá số lượng trong kho.");
                item.setQuantity(item.getQuantity() - quantityToDelivery);
                if(!itemToPush.getIsDelivered()) {
                    InventoryItem itemHolding = itemsHoldingInWarehouseMap.getOrDefault(new ObjectId(itemToPush.getId()), null);
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
                        for(var i : deliveryOrder.getInventoryItems()){
                            if(i.getId().equals(itemHolding.getId())){
                                i.setQuantity(i.getQuantity() + quantityToDelivery);
                                break;
                            }
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
                }
            }
            else{
                if(!itemToPush.getIsDelivered()) item.setStatus(InventoryItemStatus.HOLD.getId());
                else item.setQuantity(0);
                DeliveryOrder.InventoryItemDelivery itemDelivery = inventoryItemMapper.toInventoryItemDelivery(item);
                itemDelivery.setIsDelivered(itemToPush.getIsDelivered());
                deliveryOrder.getInventoryItems().add(itemDelivery);
            }
        }
        List<ObjectId> itemsQuantityZeroToDel = itemsToDelivery.stream()
                .filter(e -> e.getQuantity() == 0).map(InventoryItem::getId).toList();
        inventoryItemRepository.bulkHardDelete(itemsQuantityZeroToDel);
        inventoryItemRepository.bulkInsert(sparePartToNew);
        List<InventoryItem> itemsToUpdateStatusAndQuantity = Stream.concat(itemsToDelivery.stream(), itemsHoldingInWarehouse.stream()).toList();
        inventoryItemRepository.bulkUpdateStatusAndQuantity(itemsToUpdateStatusAndQuantity);
    }

    @Transactional
    public DeliveryOrder removeItem(DeleteItemsOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));
        if(deliveryOrder.getInventoryItems() == null || deliveryOrder.getInventoryItems().isEmpty())
            throw LogicErrException.of("Đơn hàng hiện không có mặt hàng nào");
        List<ObjectId> idsInventoryItem = dto.getItemIds().stream().map(ObjectId::new).toList();
        List<DeliveryOrder.InventoryItemDelivery> items = deliveryOrder.getInventoryItems().stream().filter(o -> idsInventoryItem.contains(o.getId())).toList();
        if(items.isEmpty()) throw LogicErrException.of("Hàng hóa trong đơn không tồn tại.");
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByIdIn(idsInventoryItem).stream().filter(e -> !e.getInventoryType().equals(InventoryType.SPARE_PART.getId())).toList();
        Map<ObjectId, InventoryItem> inventoryItemsMap = inventoryItems.stream().collect(Collectors.toMap(InventoryItem::getId, e -> e));
        List<InventoryItem> itemsToNew = new ArrayList<>();
        List<InventoryItem> itemsToUpdate = new ArrayList<>();
        List<ObjectId> itemIdsToDel = new ArrayList<>();
        for(var item : items){
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                List<InventoryItem> spareParts = inventoryItemRepository.findByCommodityCodeAndWarehouseIdList(item.getCommodityCode(), item.getWarehouseId());
                InventoryItem sparePartInStock = spareParts.stream().filter(e -> e.getStatus().equals(InventoryItemStatus.IN_STOCK)).findFirst().orElse(null);
                if(sparePartInStock != null) {
                    sparePartInStock.setQuantity(sparePartInStock.getQuantity() + item.getQuantity());
                    itemsToUpdate.add(sparePartInStock);
                    spareParts.stream().filter(e -> e.getId().equals(item.getId()) && e.getStatus().equals(InventoryItemStatus.HOLD)).findFirst()
                            .ifPresent(sparePartHolding -> itemIdsToDel.add(sparePartHolding.getId()));
                }
                else {
                    sparePartInStock = inventoryItemMapper.toInventoryItem(item);
                    sparePartInStock.setStatus(InventoryItemStatus.IN_STOCK.getId());
                    itemsToNew.add(sparePartInStock);
                }
            }
            else {
                InventoryItem product = inventoryItemsMap.getOrDefault(item.getId(), null);
                if(product != null) itemsToUpdate.add(product);
                else {
                    product = inventoryItemMapper.toInventoryItem(item);
                    itemsToNew.add(product);
                }
                product.setStatus(InventoryItemStatus.IN_STOCK.getId());
            }
            deliveryOrder.getInventoryItems().remove(item);
        }
        inventoryItemRepository.bulkHardDelete(itemIdsToDel);
        inventoryItemRepository.bulkInsert(itemsToNew);
        inventoryItemRepository.bulkUpdateStatusAndQuantity(itemsToUpdate);
        return deliveryOrderRepository.save(deliveryOrder);
    }
}
