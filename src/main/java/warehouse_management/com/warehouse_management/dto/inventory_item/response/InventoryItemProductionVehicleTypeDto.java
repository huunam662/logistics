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
    private String liftingCapacityKg;
    private String chassisType;
    private String liftingHeightMm;
    private String engineType;
}
