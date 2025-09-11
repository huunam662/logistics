package warehouse_management.com.warehouse_management.dto.repair.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class CreateRepairDTO {
    @NotNull(message = "Item không được null hoặc để trống")
    private ObjectId repairInventoryItemId;
    private String note;

    @Schema(example = "yyyy-MM-dd")
    private LocalDateTime expectedCompletionDate;
}
