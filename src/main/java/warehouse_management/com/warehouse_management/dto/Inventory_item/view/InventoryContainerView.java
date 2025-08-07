package warehouse_management.com.warehouse_management.dto.Inventory_item.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.Warehouse;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InventoryContainerView extends Container {

    private Warehouse toWarehouse;

}
