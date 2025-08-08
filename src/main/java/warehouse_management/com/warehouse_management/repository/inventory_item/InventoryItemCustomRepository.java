package warehouse_management.com.warehouse_management.repository.inventory_item;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.InventoryWarehouseContainer;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryPoWarehouseRes;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.util.Collection;
import java.util.List;

public interface InventoryItemCustomRepository {

    Page<InventoryWarehouseContainer> findPageInventoryProduction(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventoryDeparture(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventoryConsignment(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventorySparePartsProduction(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventoryDestination(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventorySparePartsDeparture(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventorySparePartsDestination(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventorySparePartsConsignment(ObjectId warehouseId, PageOptionsReq optionsReq);

    Page<InventoryWarehouseContainer> findPageInventoryCentralWarehouse(PageOptionsReq optionsReq);

    List<InventoryPoWarehouseRes> findInventoryInStockPoNumbers(String warehouseType, String filter, Sort sort);

    List<InventoryItem> findInventoryInStockByPoNumber(String warehouseType, String poNumber, String filter, Sort sort);

    void insertAll(Collection<InventoryItem> inventoryItems);

    void bulkUpdateTransferDeparture(Collection<InventoryItem> inventoryItems);
}
