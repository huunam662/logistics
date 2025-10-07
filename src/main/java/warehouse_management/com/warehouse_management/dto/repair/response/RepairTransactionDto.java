package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class RepairTransactionDto {

    private ObjectId id;
    private ObjectId repairId;          // Đơn sửa chữa cha
    private Boolean isRepaired;
    private String reason;              // Lý do sửa chữa

    private LocalDateTime repairedAt;

    private String repairedBy; // Người trực tiếp (hoán đổi, tháo rời, lắp ráp)

}
