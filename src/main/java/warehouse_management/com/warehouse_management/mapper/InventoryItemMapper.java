package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    InventoryItem toInventoryItemModel(CreateInventoryItemDto inventoryItemReq);

    InventoryItem cloneEntity(InventoryItem inventoryItem);

    @Mapping(target = "liftingCapacityKg", source = "specifications.liftingCapacityKg")
    @Mapping(target = "chassisType", source = "specifications.chassisType")
    @Mapping(target = "liftingHeightMm", source = "specifications.liftingHeightMm")
    @Mapping(target = "engineType", source = "specifications.engineType")
    InventoryItemPoNumberDto toInventoryItemPoNumberDto(InventoryItem inventoryItem);

    @Mapping(target = "id", ignore = true)
    void mapToUpdateInventoryItem(@MappingTarget InventoryItem inventoryItem, UpdateInventoryItemDto dto);
}
