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
    @Mapping(target = "specifications.batteryInfo", ignore = true)
    @Mapping(target = "specifications.batterySpecification", ignore = true)
    @Mapping(target = "specifications.chargerSpecification", ignore = true)
    @Mapping(target = "commodityCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications.engineType", ignore = true)
    @Mapping(target = "specifications.forkDimensions", ignore = true)
    @Mapping(target = "specifications.valveCount", ignore = true)
    @Mapping(target = "specifications.hasSideShift", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "specifications.otherDetails", ignore = true)
    @Mapping(target = "description", ignore = true)
    InventoryItem cloneToLiftingFrame(InventoryItem inventoryItem);



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "specifications.liftingCapacityKg", ignore = true)
    @Mapping(target = "specifications.chassisType", ignore = true)
    @Mapping(target = "specifications.liftingHeightMm", ignore = true)
    @Mapping(target = "specifications.chargerSpecification", ignore = true)
    @Mapping(target = "commodityCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications.engineType", ignore = true)
    @Mapping(target = "specifications.forkDimensions", ignore = true)
    @Mapping(target = "specifications.valveCount", ignore = true)
    @Mapping(target = "specifications.hasSideShift", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "specifications.otherDetails", ignore = true)
    @Mapping(target = "description", ignore = true)
    InventoryItem cloneToBattery(InventoryItem inventoryItem);



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "specifications.liftingCapacityKg", ignore = true)
    @Mapping(target = "specifications.chassisType", ignore = true)
    @Mapping(target = "specifications.liftingHeightMm", ignore = true)
    @Mapping(target = "specifications.batteryInfo", ignore = true)
    @Mapping(target = "specifications.batterySpecification", ignore = true)
    @Mapping(target = "commodityCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications.engineType", ignore = true)
    @Mapping(target = "specifications.forkDimensions", ignore = true)
    @Mapping(target = "specifications.valveCount", ignore = true)
    @Mapping(target = "specifications.hasSideShift", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "specifications.otherDetails", ignore = true)
    @Mapping(target = "description", ignore = true)
    InventoryItem cloneToCharger(InventoryItem inventoryItem);



    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "specifications.liftingCapacityKg", ignore = true)
    @Mapping(target = "specifications.chassisType", ignore = true)
    @Mapping(target = "specifications.liftingHeightMm", ignore = true)
    @Mapping(target = "specifications.batteryInfo", ignore = true)
    @Mapping(target = "specifications.batterySpecification", ignore = true)
    @Mapping(target = "specifications.chargerSpecification", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications.forkDimensions", ignore = true)
    @Mapping(target = "specifications.valveCount", ignore = true)
    @Mapping(target = "specifications.hasSideShift", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "specifications.otherDetails", ignore = true)
    @Mapping(target = "commodityCode", source = "serialNumber")
    @Mapping(target = "description", ignore = true)
    InventoryItem cloneToEngineType(InventoryItem inventoryItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "specifications.liftingCapacityKg", ignore = true)
    @Mapping(target = "specifications.chassisType", ignore = true)
    @Mapping(target = "specifications.liftingHeightMm", ignore = true)
    @Mapping(target = "specifications.batteryInfo", ignore = true)
    @Mapping(target = "specifications.batterySpecification", ignore = true)
    @Mapping(target = "specifications.chargerSpecification", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications.engineType", ignore = true)
    @Mapping(target = "specifications.valveCount", ignore = true)
    @Mapping(target = "specifications.hasSideShift", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "specifications.otherDetails", ignore = true)
    @Mapping(target = "commodityCode", source = "serialNumber")
    @Mapping(target = "description", ignore = true)
    InventoryItem cloneToForkDimensions(InventoryItem inventoryItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productCode", ignore = true)
    @Mapping(target = "specifications.liftingCapacityKg", ignore = true)
    @Mapping(target = "specifications.chassisType", ignore = true)
    @Mapping(target = "specifications.liftingHeightMm", ignore = true)
    @Mapping(target = "specifications.batteryInfo", ignore = true)
    @Mapping(target = "specifications.batterySpecification", ignore = true)
    @Mapping(target = "specifications.chargerSpecification", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "specifications.engineType", ignore = true)
    @Mapping(target = "specifications.forkDimensions", ignore = true)
    @Mapping(target = "specifications.valveCount", ignore = true)
    @Mapping(target = "specifications.hasSideShift", ignore = true)
    @Mapping(target = "pricing", ignore = true)
    @Mapping(target = "specifications.otherDetails", ignore = true)
    @Mapping(target = "commodityCode", source = "serialNumber")
    @Mapping(target = "description", ignore = true)
    InventoryItem cloneToValveOrSideShift(InventoryItem inventoryItem);
}
