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
@Document(collection = "warranty_transaction")
public class WarrantyTransaction implements Persistable<ObjectId> {
    @Id
    private ObjectId id;
    private ObjectId warrantyId;        // Phiếu bảo hành cha
    private String sparePartWarranty;   // Bộ phận cần bảo hành
    private String reason;              // Lý do bảo hành
    private String createByName;        // Username của người tạo phiếu bảo hành
    private String updateByName;        // Username của người cập nhật phiếu bảo hành
    private Boolean isCompleted;        // Trạng thái hoàn thành của phiếu

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

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
