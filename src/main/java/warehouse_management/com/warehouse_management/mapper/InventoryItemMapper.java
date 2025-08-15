package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.model.InventoryItem;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    @Mapping(target = "logistics.orderDate", ignore = true)
    @Mapping(target = "logistics.estimateCompletionDate", ignore = true)
    InventoryItem toInventoryItemModel(CreateInventoryProductDto inventoryItemReq);

    InventoryItem toInventoryItemSparePart(CreateInventorySparePartDto dto);

    InventoryItem cloneEntity(InventoryItem inventoryItem);

    InventoryItemPoNumberDto toInventoryItemPoNumberDto(InventoryItem inventoryItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "logistics.orderDate", ignore = true)
    @Mapping(target = "logistics.estimateCompletionDate", ignore = true)
    void mapToUpdateInventoryProduct(@MappingTarget InventoryItem inventoryItem, UpdateInventoryProductDto dto);

    @Mapping(target = "id", ignore = true)
    void mapToUpdateInventorySparePart(@MappingTarget InventoryItem inventoryItem, UpdateInventorySparePartDto dto);

    InventoryProductDetailsDto toInventoryProductDetailsDto(InventoryItem inventoryItem);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventorySparePartDetailsDto toInventorySparePartDetailsDto(InventoryItem inventoryItem);
}
