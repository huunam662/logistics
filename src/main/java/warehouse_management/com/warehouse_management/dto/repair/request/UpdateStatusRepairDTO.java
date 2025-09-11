package warehouse_management.com.warehouse_management.dto.repair.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;

@Data
public class UpdateStatusRepairDTO {
    @NotNull
    private ObjectId repairId;
    @NotNull
    private RepairStatus status;
}
