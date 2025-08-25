package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class InventoryItemCodeQuantityDto {
    private ObjectId id;
    private String productCode;
    private String commodityCode;
    private Integer quantity;
}
