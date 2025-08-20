package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class InventoryItemPoNumberDto {
    private ObjectId id;
    private String poNumber;
    private String productCode;
    private String commodityCode;
    private String serialNumber;
    private String model;
    private Integer quantity;
    private String description;
    private String inventoryType;
}
