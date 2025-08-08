package warehouse_management.com.warehouse_management.repository.inventory_item;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.common.pagination.req.PageOptionsReq;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemProductionVehicleTypeDto;
import warehouse_management.com.warehouse_management.model.InventoryItem;


public interface CustomInventoryItemRepository {
    Page<InventoryItemProductionVehicleTypeDto> getItemsFromVehicleWarehouse(ObjectId warehouseId, PageOptionsReq optionsReq);
}
