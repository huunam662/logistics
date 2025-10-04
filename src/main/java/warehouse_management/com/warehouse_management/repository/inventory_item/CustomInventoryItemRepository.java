package warehouse_management.com.warehouse_management.repository.inventory_item;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairVehicleSpecPageDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.Collection;
import java.util.List;


public interface CustomInventoryItemRepository {
    Page<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryProductionDto> findPageInventoryProduction(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryDepartureDto> findPageInventoryDeparture(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryConsignmentDto> findPageInventoryConsignment(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryProductionSparePartsDto> findPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryDestinationDto> findPageInventoryDestination(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryDepartureSparePartsDto> findPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryDestinationSparePartsDto> findPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryConsignmentSparePartsDto> findPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsDto optionsReq);

    Page<InventoryCentralWarehouseProductDto> findPageInventoryCentralWarehouse(PageOptionsDto optionsReq);

    Page<InventoryCentralWarehouseProductDto> findPageInventoryCentralWarehouseConsignment(PageOptionsDto optionsReq);

    Page<InventoryCentralWarehouseSparePartDto> findPageInventoryCentralWarehouseSparePart(PageOptionsDto optionsReq);

    Page<InventoryCentralWarehouseSparePartDto> findPageInventoryCentralWarehouseConsignmentSparePart(PageOptionsDto optionsReq);

    List<InventoryPoWarehouseDto> findPoNumbersOfInventoryInStock(String warehouseType, List<String> inventoryTypes, String model, String warehouseId);

    List<InventoryItemPoNumberDto> findInventoryInStockByPoNumber(String warehouseType, String warehouseId, String poNumber, String filter);

    List<InventoryItem> bulkInsert(Collection<InventoryItem> inventoryItems);

    void bulkUpdateTransfer(Collection<InventoryItem> inventoryItems);

    void bulkUpdateStatusAndQuantity(Collection<InventoryItem> inventoryItems);

    void bulkUpdateSpecAndPricing(Collection<InventoryItem> inventoryItems);

    void bulkUpdateComponentSerial(Collection<InventoryItem> inventoryItems);

    void updateStatusAndUnRefContainer(Collection<ObjectId> ids, String status);

    void updateStatusAndWarehouseAndUnRefContainer(Collection<ObjectId> ids, ObjectId warehouseId, String status);

    void updateStatusByIdIn(Collection<ObjectId> ids, String status);

    void updateStatusAndWarehouseByIdIn(Collection<ObjectId> ids, ObjectId warehouseId, String status);

    long softDelete(ObjectId id, ObjectId deletedBy);

    long bulkSoftDelete(Collection<ObjectId> ids, ObjectId deletedBy);

    long bulkHardDelete(Collection<ObjectId> ids);

    List<InventoryItemModelDto> findAllModelsAndItems(List<String> inventoryTypes, List<ObjectId> warehouseIds);

    Page<ReportInventoryDto> findPageReportItemProductionConsignmentToDashBoard(ReportParamsDto params);

    Page<ReportInventoryDto> findPageReportItemInTransitContainerToDashBoard(ReportParamsDto params);

    List<InventoryProductDetailsDto> findProductsByWarehouseId(ObjectId warehouseId, String filter);

    List<InventoryProductDetailsDto> findProductsByWarehouseIdIn(List<ObjectId> warehouseIds, String filter);

    List<InventorySparePartDetailsDto> findSparePartByWarehouseId(ObjectId warehouseId, String filter);

    List<InventorySparePartDetailsDto> findSparePartByWarehouseIdIn(List<ObjectId> warehouseIds, String filter);

    List<InventoryProductDetailsDto> findVehicles(PageOptionsDto optionsReq);

    Page<InventoryItemWarrantyDto> findItemForWarranty(PageOptionsDto optionsDto);

    Page<InventoryItemRepairDto> findItemForRepair(PageOptionsDto optionsDto);

    Page<ConfigVehicleSpecPageDto> findPageConfigVehicleSpec(PageOptionsDto optionsDto);

    Page<RepairVehicleSpecPageDto> findPageRepairVehicleSpec(PageOptionsDto optionsDto);

    Page<ItemCodeModelSerialDto> findPageVehicleInStock(PageOptionsDto optionsDto);
}
