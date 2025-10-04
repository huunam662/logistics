package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;

@Data
public class RepairDisassembleComponentDto {

    private String vehicleId;    // ID xe
    private String componentType;     // Loại bộ phận: frame, valve, fork, engine, wheel, battery, sideshift, charge
    private String repairCode;
}
