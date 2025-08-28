package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.CreateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.UpdateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryItemModelDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryProductDetailsDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliverySparePartDetailsDto;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DeliveryOrderMapper {

    @Mapping(target = "customerId", ignore = true)
    DeliveryOrder toCreateDeliveryOrder(CreateDeliveryOrderDto dto);

    @Mapping(target = "customerId", ignore = true)
    void mapToUpdateDeliveryOrder(@MappingTarget DeliveryOrder deliveryOrder, UpdateDeliveryOrderDto dto);

    DeliveryItemModelDto toDeliveryOrderItemsDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);

    DeliveryProductDetailsDto toDeliveryProductDetailsDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);

    DeliverySparePartDetailsDto toDeliverySparePartDetailsDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);
}
