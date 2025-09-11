package warehouse_management.com.warehouse_management.dto.repair.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class RepairTransactionResponseDTO {
    private ObjectId id;
    private ObjectId repairId;          // Đơn sửa chữa cha
    private String sparePartRepair;     // Bộ phận cần sửa chữa
    private String reason;              // Lý do sửa chữa
    private String createByName;        // Username của người tạo đơn sửa chữa

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
