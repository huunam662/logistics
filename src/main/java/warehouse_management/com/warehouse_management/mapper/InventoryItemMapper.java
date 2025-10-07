package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.*;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionProductDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport.ExcelImportProductionSparePartDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface InventoryItemMapper {

    @Mapping(target = "warehouseId", ignore = true)
    @Mapping(target = "logistics.orderDate", ignore = true)
    @Mapping(target = "logistics.estimateCompletionDate", ignore = true)
    InventoryItem toInventoryItemModel(CreateInventoryProductDto inventoryItemReq);

    @Mapping(target = "warehouseId", ignore = true)
    InventoryItem toInventoryItemSparePart(CreateInventorySparePartDto dto);

    InventoryItem cloneEntity(InventoryItem inventoryItem);

    InventoryProductDetailsDto toInventoryProductDetailsDto(InventoryItem inventoryItem);

    @Mapping(target = "orderDate", source = "logistics.orderDate")
    InventorySparePartDetailsDto toInventorySparePartDetailsDto(InventoryItem inventoryItem);

    Container.InventoryItemContainer toInventoryItemContainer(InventoryItem inventoryItem);

    WarehouseTransaction.InventoryItemTicket toInventoryItemTicket(InventoryItem inventoryItem);

    InventoryProductDetailsDto toInventoryProductDetailsDto(Container.InventoryItemContainer dto);

    InventorySparePartDetailsDto toInventorySparePartDetailsDto(Container.InventoryItemContainer dto);

    InventoryItem toInventoryItem(ExcelImportProductionProductDto dto);

    InventoryItem toInventoryItem(ExcelImportProductionSparePartDto dto);

    WarehouseTransaction.InventoryItemTicket toInventoryItemTicket(ExcelImportProductionProductDto dto);

    WarehouseTransaction.InventoryItemTicket toInventoryItemTicket(ExcelImportProductionSparePartDto dto);

    DeliveryOrder.InventoryItemDelivery toInventoryItemDelivery(InventoryItem inventoryItem);

    InventoryItem toInventoryItem(DeliveryOrder.InventoryItemDelivery item);

    InventoryItem toInventoryItem(Container.InventoryItemContainer item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications", ignore = true)
    @Mapping(target = "specificationsBase", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "serialNumber", ignore = true)
    @Mapping(target = "notes", ignore = true)
    @Mapping(target = "isFullyComponent", ignore = true)
    @Mapping(target = "initialCondition", ignore = true)
    InventoryItem cloneToComponent(InventoryItem inventoryItem);
}
