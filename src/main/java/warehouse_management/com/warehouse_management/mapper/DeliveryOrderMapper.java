package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.CreateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.dto.delivery_order.request.UpdateDeliveryOrderDto;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface DeliveryOrderMapper {

    @Mapping(target = "customerId", ignore = true)
    DeliveryOrder toCreateDeliveryOrder(CreateDeliveryOrderDto dto);

    @Mapping(target = "customerId", ignore = true)
    void mapToUpdateDeliveryOrder(@MappingTarget DeliveryOrder deliveryOrder, UpdateDeliveryOrderDto dto);
}
