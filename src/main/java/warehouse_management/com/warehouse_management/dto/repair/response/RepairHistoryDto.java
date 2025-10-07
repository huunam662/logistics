package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class RepairHistoryDto {

    private ObjectId id;
    private String repairCode;
    private ObjectId vehicleId;                 // Sản phẩm được bảo hành
    private ObjectId componentId;
    private String componentSerialNumber;
    private String componentType;
    private String componentName;
    private String description;                                        // Ghi chú cho đơn bảo hành
    private String repairType;                  // Kiểu sửa chữa

    private LocalDate expectedCompletionDate;                   // Ngày dự kiến hoàn thành

    private LocalDateTime completedAt;

    private LocalDateTime performedAt;

    private String completedBy; // Người trực tiếp (hoán đổi, tháo rời, lắp ráp)

    private String performedBy; // Tên người thao tác

}
