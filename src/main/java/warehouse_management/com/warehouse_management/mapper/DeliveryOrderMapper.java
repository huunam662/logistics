package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.CreateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.UpdateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.BackDeliveryProductModelDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryProductTickDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.BackDeliverySparePartModelDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliverySparePartTickDto;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.model.InventoryItem;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DeliveryOrderMapper {

    @Mapping(target = "customerId", ignore = true)
    DeliveryOrder toCreateDeliveryOrder(CreateDeliveryOrderDto dto);

    @Mapping(target = "customerId", ignore = true)
    void mapToUpdateDeliveryOrder(@MappingTarget DeliveryOrder deliveryOrder, UpdateDeliveryOrderDto dto);

    DeliveryProductTickDto toDeliveryProductTickDto(DeliveryOrder.InventoryItemDelivery orderItemDelivery);

    DeliverySparePartTickDto toDeliverySparePartTickDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);

    BackDeliveryProductModelDto toBackDeliveryProductModelDto(DeliveryOrder.BackDeliveryModel backDeliveryModel);

    BackDeliverySparePartModelDto toBackDeliverySparePartModelDto(DeliveryOrder.BackDeliveryModel backDeliveryModel);

    DeliveryOrder.InventoryItemDelivery toInventoryItemDelivery(InventoryItem inventoryItem);

    InventoryItem toInventoryItem(DeliveryOrder.InventoryItemDelivery item);

}
