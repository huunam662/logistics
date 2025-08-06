package warehouse_management.com.warehouse_management.module.warehouse.dto.view;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import warehouse_management.com.warehouse_management.model.User;
import warehouse_management.com.warehouse_management.model.Warehouse;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WarehouseView extends Warehouse {

    private User userManaged;

}
