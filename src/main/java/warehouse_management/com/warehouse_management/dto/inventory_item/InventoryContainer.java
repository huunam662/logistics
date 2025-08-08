package warehouse_management.com.warehouse_management.dto.inventory_item;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.Warehouse;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class InventoryContainer extends Container {

    private Warehouse toWarehouse;

}
