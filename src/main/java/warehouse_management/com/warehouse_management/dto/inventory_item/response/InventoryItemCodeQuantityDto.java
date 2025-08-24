package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

@Data
public class InventoryItemCodeQuantityDto {
    private String poNumber;
    private String model;
    private String code;
    private Integer quantity;
}
