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
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "repair_transaction")
public class RepairTransaction {
    @Id
    private ObjectId id;
    private ObjectId repairId;          // Phiếu sửa chữa cha
    private String sparePartRepair;     // Bộ phận cần sửa chữa
    private String reason;              // Lý do sửa chữa
    private String createByName;        // Username của người tạo phiếu sửa chữa

    @CreatedDate
    private LocalDateTime createdAt;
    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    private LocalDateTime updatedAt;
    @LastModifiedBy
    private String updatedBy;

    private LocalDateTime deletedAt;
    private ObjectId deletedBy;
}
