package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateInventorySparePartDto extends CreateInventorySparePartDto{

    private String id;

}
