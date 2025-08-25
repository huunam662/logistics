package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

@Data
public class InventoryItemCodeQuantityDto {
    private String productCode;
    private String commodityCode;
    private Integer quantity;
}
