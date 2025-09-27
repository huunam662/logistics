package warehouse_management.com.warehouse_management.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
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

    @Mapping(target = "inventoryItemId", source = "id")
    @Mapping(target = "isDelivered", source = "isDelivered")
    DeliveryItemModelDto toDeliveryOrderItemsDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);

    @Mapping(target = "warehouseId", source="warehouseId")
    DeliveryProductDetailsDto toDeliveryProductDetailsDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);

    @Mapping(target = "warehouseId", source="warehouseId")
    DeliverySparePartDetailsDto toDeliverySparePartDetailsDto(DeliveryOrder.InventoryItemDelivery inventoryItemDelivery);

    @Mapping(target = "commodityCode", source = ".", qualifiedByName = "mapCommodityCode")
    @Mapping(target = "isDelivered", source = ".", qualifiedByName = "mapIsDelivered")
    DeliverySparePartDetailsDto toDeliverySparePartNotesDto(DeliveryOrder.NoteDeliveryModel noteDeliveryModel);

    @Mapping(target = "productCode", source = ".", qualifiedByName = "mapProductCode")
    @Mapping(target = "isDelivered", source = ".", qualifiedByName = "mapIsDelivered")
    DeliveryProductDetailsDto toDeliveryProductDetailsDto(DeliveryOrder.NoteDeliveryModel noteDeliveryModel);

    @Named("mapCommodityCode")
    default String mapCommodityCode(DeliveryOrder.NoteDeliveryModel noteDeliveryModel) {
        return Boolean.TRUE.equals(noteDeliveryModel.getIsSparePart())
                ? noteDeliveryModel.getModel()
                : null;
    }

    @Named("mapProductCode")
    default String mapProductCode(DeliveryOrder.NoteDeliveryModel noteDeliveryModel) {
        return Boolean.TRUE.equals(noteDeliveryModel.getIsSparePart())
                ? null
                : noteDeliveryModel.getModel();
    }

    @Named("mapIsDelivered")
    default Boolean mapIsDelivered(DeliveryOrder.NoteDeliveryModel noteDeliveryModel) {
        return Boolean.FALSE;
    }

    default ObjectId map(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return new ObjectId(value);
    }
}
