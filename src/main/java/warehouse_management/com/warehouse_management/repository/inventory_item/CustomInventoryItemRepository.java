package warehouse_management.com.warehouse_management.repository.inventory_item;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.*;
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

    Page<InventoryCentralWarehouseDto> findPageInventoryCentralWarehouse(PageOptionsDto optionsReq);

    List<InventoryPoWarehouseDto> findPoNumbersOfInventoryInStock(String warehouseType, List<String> inventoryTypes);

    List<InventoryItemPoNumberDto> findInventoryInStockByPoNumber(String warehouseType, String poNumber, String filter);

    List<InventoryItem> insertAll(Collection<InventoryItem> inventoryItems);

    void bulkUpdateTransfer(Collection<InventoryItem> inventoryItems);

    void updateStatusAndUnRefContainer(ObjectId containerId, String status);

    long softDelete(ObjectId id, ObjectId deletedBy);

    long bulkSoftDelete(Collection<ObjectId> ids, ObjectId deletedBy);
}
