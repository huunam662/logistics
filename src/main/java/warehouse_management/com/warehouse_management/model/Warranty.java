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
import warehouse_management.com.warehouse_management.enumerate.WarrantyStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "warranty")
public class Warranty implements Persistable<ObjectId> {
    @Id
    private ObjectId id;
    private InventoryItem warrantyInventoryItem;                // Sản phẩm được bảo hành
    private Client client;                                      // Khách hàng của sản phẩm được bảo hành
    private String note;                                        // Ghi chú cho đơn bảo hành
    private WarrantyStatus status;                              // Trạng thái của đơn bảo hành
    private List<WarrantyTransaction> warrantyTransactions;     // Phiếu bảo hành của đơn bảo hành
    private LocalDateTime completedDate;                        // Ngày hoàn thành

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private ObjectId deletedBy;
    private LocalDateTime deletedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}
