package warehouse_management.com.warehouse_management.dto.warranty.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.enumerate.WarrantyStatus;

@Data
public class UpdateStatusWarrantyRequestDTO {
    @NotNull
    private ObjectId warrantyId;
    @NotNull
    private WarrantyStatus status;
}
