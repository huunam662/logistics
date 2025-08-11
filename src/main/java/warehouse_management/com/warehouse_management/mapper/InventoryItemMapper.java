package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    InventoryItem toInventoryItemModel(CreateInventoryItemDto inventoryItemReq);

    InventoryItem.Specifications toInventoryItemModel(CreateInventoryItemDto.Specifications inventoryItemReq);

    InventoryItem.Pricing toInventoryItemModel(CreateInventoryItemDto.Pricing inventoryItemReq);

    InventoryItem.Logistics toInventoryItemModel(CreateInventoryItemDto.Logistics inventoryItemReq);

    InventoryItem cloneEntity(InventoryItem inventoryItem);

}
