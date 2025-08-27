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

    public DeliveryOrderProductTicksDto getDeliveryOrderProductTicks(ObjectId id){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        DeliveryOrderProductTicksDto deliveryOrderTicksDto = new DeliveryOrderProductTicksDto();
        if(deliveryOrder.getInventoryItems() == null) deliveryOrder.setInventoryItems(List.of());
        for(var item : deliveryOrder.getInventoryItems()){
            if(!item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                DeliveryProductTickDto productTickDto = deliveryOrderMapper.toDeliveryProductTickDto(item);
                deliveryOrderTicksDto.getProductTicks().add(productTickDto);
            }
        }
        if(deliveryOrder.getBackDeliveryModels() == null) deliveryOrder.setBackDeliveryModels(List.of());
        for(var outstandingModel : deliveryOrder.getBackDeliveryModels()){
            if(!outstandingModel.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                BackDeliveryProductModelDto backDeliveryModel = deliveryOrderMapper.toBackDeliveryProductModelDto(outstandingModel);
                deliveryOrderTicksDto.getBackDeliveryProductModels().add(backDeliveryModel);
            }
        }
        return deliveryOrderTicksDto;
    }

    public DeliveryOrderSparePartTicksDto getDeliveryOrderSparePartTicks(ObjectId id){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        DeliveryOrderSparePartTicksDto deliveryOrderTicksDto = new DeliveryOrderSparePartTicksDto();
        if(deliveryOrder.getInventoryItems() == null) deliveryOrder.setInventoryItems(List.of());
        for(var item : deliveryOrder.getInventoryItems()){
            if(item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                DeliverySparePartTickDto sparePartTickDto = deliveryOrderMapper.toDeliverySparePartTickDto(item);
                deliveryOrderTicksDto.getSparePartTicks().add(sparePartTickDto);
            }
        }
        if(deliveryOrder.getBackDeliveryModels() == null) deliveryOrder.setBackDeliveryModels(List.of());
        for(var outstandingModel : deliveryOrder.getBackDeliveryModels()){
            if(outstandingModel.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
                BackDeliverySparePartModelDto backDeliveryModel = deliveryOrderMapper.toBackDeliverySparePartModelDto(outstandingModel);
                deliveryOrderTicksDto.getBackDeliverySparePartModels().add(backDeliveryModel);
            }
        }
        return deliveryOrderTicksDto;
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
        if(deliveryOrder.getBackDeliveryModels() == null) deliveryOrder.setBackDeliveryModels(new ArrayList<>());
        List<ObjectId> pushItemIds = itemsToDeliveryDto.stream().filter(e -> e.getId() != null).map(e -> new ObjectId(e.getId())).toList();
        List<InventoryItem> itemsToDelivery = inventoryItemRepository.findByIdIn(pushItemIds);
        Map<ObjectId, InventoryItem> itemsToDeliveryMap = itemsToDelivery.stream().collect(Collectors.toMap(InventoryItem::getId, e -> e));
        List<InventoryItem> itemsHoldingInWarehouse = inventoryItemRepository.findSparePartByCommodityCodeIn(pushItemIds, InventoryItemStatus.HOLD.getId());
        Map<ObjectId, InventoryItem> itemsHoldingInWarehouseMap = itemsHoldingInWarehouse.stream().collect(Collectors.toMap(InventoryItem::getId, e -> e));
        List<InventoryItem> sparePartToNew = new ArrayList<>();
        for(var itemToPush : itemsToDeliveryDto){
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
                        DeliveryOrder.InventoryItemDelivery itemDelivery = deliveryOrderMapper.toInventoryItemDelivery(sparePartClone);
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
                        DeliveryOrder.InventoryItemDelivery sparePartDelivery = deliveryOrderMapper.toInventoryItemDelivery(item);
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
                else item.setStatus(InventoryItemStatus.SOLD.getId());
                DeliveryOrder.InventoryItemDelivery itemDelivery = deliveryOrderMapper.toInventoryItemDelivery(item);
                itemDelivery.setIsDelivered(itemToPush.getIsDelivered());
                deliveryOrder.getInventoryItems().add(itemDelivery);
            }
        }
        List<ObjectId> itemsQuantityZeroToDel = itemsToDelivery.stream()
                .filter(e -> e.getQuantity() == 0 && e.getInventoryType().equals(InventoryType.SPARE_PART.getId()))
                .map(InventoryItem::getId).toList();
        inventoryItemRepository.bulkHardDelete(itemsQuantityZeroToDel);
        inventoryItemRepository.bulkInsert(sparePartToNew);
        List<InventoryItem> itemsToUpdateStatusAndQuantity = Stream.concat(itemsToDelivery.stream(), itemsHoldingInWarehouse.stream()).toList();
        inventoryItemRepository.bulkUpdateStatusAndQuantity(itemsToUpdateStatusAndQuantity);
    }

    @Transactional
    public DeliveryOrder removeItem(DeleteItemsOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(new ObjectId(dto.getDeliveryOrderId()));
        if(deliveryOrder.getInventoryItems() == null)
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
                    sparePartInStock = deliveryOrderMapper.toInventoryItem(item);
                    sparePartInStock.setStatus(InventoryItemStatus.IN_STOCK.getId());
                    itemsToNew.add(sparePartInStock);
                }
            }
            else {
                InventoryItem product = inventoryItemsMap.getOrDefault(item.getId(), null);
                if(product != null) itemsToUpdate.add(product);
                else {
                    product = deliveryOrderMapper.toInventoryItem(item);
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
