package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

@Data
public class InventoryItemPoNumberDto {
    private String id;
    private String poNumber;
    private String productCode;
    private String commodityCode;
    private String serialNumber;
    private String model;
    private String status;
    private Integer manufacturingYear;
    private Integer liftingCapacityKg;
    private Integer quantity;
    private String chassisType;
    private Integer liftingHeightMm;
    private String engineType;
    private String inventoryType;
}
