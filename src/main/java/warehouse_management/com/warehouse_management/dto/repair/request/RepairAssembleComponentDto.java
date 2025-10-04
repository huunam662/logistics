package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RepairAssembleComponentDto {
    private String vehicleId;
    private String componentId;
    private String repairCode;
}
