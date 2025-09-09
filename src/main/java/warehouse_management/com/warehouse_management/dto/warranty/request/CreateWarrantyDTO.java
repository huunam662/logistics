package warehouse_management.com.warehouse_management.dto.warranty.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CreateWarrantyDTO {
    @NotNull(message = "Item không được null hoặc để trống")
    ObjectId warrantyInventoryItemId;
    String note;
}
