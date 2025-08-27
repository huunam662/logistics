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

    public DeliveryOrder getDeliveryOrderToId(ObjectId id) {
        return deliveryOrderRepository.findById(id)
                .orElseThrow(() -> LogicErrException.of("Đơn giao hàng không tồn tại."));
    }

    @Transactional
    public DeliveryOrder createDeliveryOrder(CreateDeliveryOrderDto dto) {
        DeliveryOrder deliveryOrder = deliveryOrderRepository.findByCode(dto.getDeliveryOrderCode()).orElse(null);
        if(deliveryOrder != null) throw LogicErrException.of("Mã đơn " + dto.getDeliveryOrderCode() + " đã tồn tại.");
        //TODO: Tìm khách hàng từ bảng user và gán id vào customerId

        deliveryOrder = deliveryOrderMapper.toCreateDeliveryOrder(dto);
        deliveryOrder.setStatus(DeliveryOrderStatus.UN_DELIVERED.getValue());
        return deliveryOrderRepository.save(deliveryOrder);
    }

    @Transactional
    public DeliveryOrder updateDeliveryOrder(ObjectId id, UpdateDeliveryOrderDto dto){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(id);
        if(!deliveryOrder.getStatus().equals(DeliveryOrderStatus.UN_DELIVERED.getValue())
                || deliveryOrder.getInventoryItems() != null && !deliveryOrder.getInventoryItems().isEmpty()){
            throw LogicErrException.of("Theo FRS: Đơn hàng chỉ có thể sửa khi chưa có sản phẩm và ở trạng thái \"Chưa giao\"");
        }
        DeliveryOrder deliveryOrderByCode = deliveryOrderRepository.findByCode(dto.getDeliveryOrderCode()).orElse(null);
        if(deliveryOrderByCode != null && !deliveryOrder.getId().equals(deliveryOrderByCode.getId()))
            throw LogicErrException.of("Mã đơn " + dto.getDeliveryOrderCode() + " đã tồn tại.");
//        if(!deliveryOrder.getCustomerId().toString().equals(dto.getCustomerId())){
//            //TODO: Kiểm tra sự tồn tại của khách hàng từ bảng user và gán lại customerId nếu cần cập nhật khách hàng liên quan
//        }
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
        List<PushItemToDeliveryDto> itemsToDeliveryDto = dto.getInventoryItemsDelivery();
        if(itemsToDeliveryDto == null)
            throw LogicErrException.of("Sản phẩm cần thêm vào đơn hàng hiện đang rỗng.");
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
            if(item == null) throw LogicErrException.of("Mặt hàng có Model " + itemToPush.getModel() + " không tồn tại.");
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
                        deliveryOrder.getInventoryItems().add(deliveryOrderMapper.toInventoryItemDelivery(sparePartClone));
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
                            s -> s.getCommodityCode().equals(item.getCommodityCode()) && s.getIsDelivered()
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
                else item.setQuantity(0);
                DeliveryOrder.InventoryItemDelivery itemDelivery = deliveryOrderMapper.toInventoryItemDelivery(item);
                itemDelivery.setQuantity(1);
                deliveryOrder.getInventoryItems().add(itemDelivery);
            }
            Optional<DeliveryOrder.BackDeliveryModel> backDeliveryModelOp = deliveryOrder.getBackDeliveryModels().stream().filter(b -> b.getModel().equals(item.getModel())).findFirst();
            if(backDeliveryModelOp.isPresent() && itemToPush.getIsDelivered()){
                DeliveryOrder.BackDeliveryModel backDeliveryModel = backDeliveryModelOp.get();
                if(backDeliveryModel.getQuantity() > itemToPush.getQuantity())
                    backDeliveryModel.setQuantity(backDeliveryModel.getQuantity() - itemToPush.getQuantity());
                else deliveryOrder.getBackDeliveryModels().remove(backDeliveryModel);
            }
        }
        List<ObjectId> itemsQuantityZeroToDel = itemsToDelivery.stream().filter(e -> e.getQuantity() == 0).map(InventoryItem::getId).toList();
        inventoryItemRepository.bulkHardDelete(itemsQuantityZeroToDel);
        inventoryItemRepository.insertAll(sparePartToNew);
        List<InventoryItem> itemsToUpdateStatusAndQuantity = Stream.concat(itemsToDelivery.stream(), itemsHoldingInWarehouse.stream()).toList();
        inventoryItemRepository.bulkUpdateStatusAndQuantity(itemsToUpdateStatusAndQuantity);
    }

    @Transactional
    public void deleteItem(ObjectId deliveryOrderId, ObjectId id){
        DeliveryOrder deliveryOrder = getDeliveryOrderToId(deliveryOrderId);
        if(deliveryOrder == null) return;
        Optional<DeliveryOrder.InventoryItemDelivery> itemOp = deliveryOrder.getInventoryItems().stream().filter(o -> o.getId().equals(id)).findFirst();
        if(itemOp.isEmpty()) throw LogicErrException.of("Hàng hóa trong đơn không tồn tại.");
        DeliveryOrder.InventoryItemDelivery item = itemOp.get();
        if(!item.getInventoryType().equals(InventoryType.SPARE_PART.getId())){
            InventoryItem sparePartInStock = inventoryItemRepository.findByCommodityCode(item.getCommodityCode()).orElse(null);
            if(sparePartInStock != null){
                sparePartInStock.setQuantity(sparePartInStock.getQuantity() + item.getQuantity());
                inventoryItemRepository.save(sparePartInStock);
            }
        }

    }
}
