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
@Document(collection = "warranty_transaction")
public class WarrantyTransaction {
    @Id
    private ObjectId id;
    private ObjectId warranty;          // Phiếu bảo hành cha
    private String sparePartWarranty;   // Bộ phận cần bảo hành
    private String reason;              // Lý do bảo hành

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
}
