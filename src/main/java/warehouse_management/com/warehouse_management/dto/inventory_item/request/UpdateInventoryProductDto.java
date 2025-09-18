package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;

@Data
public class UpdateInventoryProductDto {
    private String poNumber;
    private String productCode;
    private String serialNumber;
}
