package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class RepairTransactionResponseDto {
    private ObjectId id;
    private ObjectId repairId;          // Đơn sửa chữa cha
    private String sparePartRepair;     // Bộ phận cần sửa chữa
    private String reason;              // Lý do sửa chữa
    private String createByName;        // Username của người tạo đơn sửa chữa

    
    private LocalDateTime createdAt;
}
