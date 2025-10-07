package warehouse_management.com.warehouse_management.dto.repair.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRepairDto {
    @NotNull
    private String repairId;
    @NotNull
    private String status;
}
