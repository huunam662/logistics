package warehouse_management.com.warehouse_management.repository.inventory_item;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.Collection;
import java.util.List;


public interface CustomInventoryItemRepository {
    Page<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryProductionDto> findPageInventoryProduction(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryDepartureDto> findPageInventoryDeparture(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryConsignmentDto> findPageInventoryConsignment(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryProductionSparePartsDto> findPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryDestinationDto> findPageInventoryDestination(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryDepartureSparePartsDto> findPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryDestinationSparePartsDto> findPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryConsignmentSparePartsDto> findPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryCentralWarehouseDto> findPageInventoryCentralWarehouse(PageOptionsReq optionsReq);

    List<InventoryPoWarehouseDto> findInventoryInStockPoNumbers(String warehouseType, String filter, Sort sort);

    List<InventoryItemProductionVehicleTypeDto> findInventoryInStockByPoNumber(String warehouseType, String poNumber, String filter, Sort sort);

    void insertAll(Collection<InventoryItem> inventoryItems);

    void bulkUpdateTransferDeparture(Collection<InventoryItem> inventoryItems);
}
