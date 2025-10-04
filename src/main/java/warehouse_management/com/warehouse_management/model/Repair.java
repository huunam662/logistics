package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse_management.com.warehouse_management.enumerate.RepairStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "repair")
public class Repair implements Persistable<ObjectId> {

    @Id
    private ObjectId id;
    private String repairCode;
    private ObjectId vehicleId;                 // Sản phẩm được bảo hành
    private ObjectId componentId;
    private String componentSerialNumber;
    private String componentType;
    private String description;                                        // Ghi chú cho đơn bảo hành
    private String status;
    private String repairType;                  // Kiểu sửa chữa

    private LocalDate expectedCompletionDate;                   // Ngày dự kiến hoàn thành

    private LocalDateTime confirmedAt;

    private LocalDateTime completedAt;

    private LocalDateTime performedAt;

    private String confirmedBy;

    private String completedBy; // Người trực tiếp (hoán đổi, tháo rời, lắp ráp)

    private String performedBy; // Tên người thao tác

    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    private ObjectId deletedBy;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    public Repair(String repairCode) {
        this.repairCode = "RPV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + ThreadLocalRandom.current().nextInt(10000, 100000);
    }
}
