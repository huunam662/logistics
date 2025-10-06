package warehouse_management.com.warehouse_management.dto.repair.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTransactionStatusDto {
    @NotNull
    private String repairTransactionId;
    @NotNull
    private Boolean isRepaired;
}
