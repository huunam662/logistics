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

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "repair_transaction")
public class RepairTransaction implements Persistable<ObjectId> {

    @Id
    private ObjectId id;
    private ObjectId repairId;
    private Boolean isRepaired;
    private String reason;              // Lý do sửa chữa

    private LocalDateTime repairedAt;

    private String repairedBy; // Người trực tiếp (hoán đổi, tháo rời, lắp ráp)

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
}
