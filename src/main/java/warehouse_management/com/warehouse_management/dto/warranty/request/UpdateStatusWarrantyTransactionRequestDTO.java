package warehouse_management.com.warehouse_management.dto.warranty.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class UpdateStatusWarrantyTransactionRequestDTO {
    @NotNull
    private ObjectId warrantyTransactionId;
    @NotNull
    private Boolean isCompleted;
}
