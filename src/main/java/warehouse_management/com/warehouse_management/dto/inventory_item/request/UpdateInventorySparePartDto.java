package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;

@Data
public class UpdateInventorySparePartDto {
    private String poNumber;
    private String commodityCode;
    private String contactNumber;
}
