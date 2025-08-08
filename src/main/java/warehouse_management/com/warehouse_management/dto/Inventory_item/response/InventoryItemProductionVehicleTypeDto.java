package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

@Data
public class InventoryItemProductionVehicleTypeDto {
    private String id;
    private String productCode;
    private String serialNumber;
    private String model;
    private String status;
    private Integer manufacturingYear;
    private Integer liftingCapacityKg;
    private String chassisType;
    private Integer liftingHeightMm;
    private String engineType;
}
