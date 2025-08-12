package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse_management.com.warehouse_management.enumerate.TransferTicketStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "warehouse_transfer_ticket")
public class WarehouseTransferTicket {

    @Id
    private ObjectId id; // _id – Khóa chính

    private String status; // PENDING, APPROVED, REJECTED

    private List<ObjectId> inventoryItemIds;    // Id các mặt hàng cần duyệt

    private ObjectId originWarehouseId;     // Kho nguồn
    private ObjectId destinationWarehouseId; // Kho đích

    private ObjectId requesterId;               // Người tạo yêu cầu
    private ObjectId approverId;                // Người duyệt hoặc từ chối

    private String rejectReason;              // Lý do từ chối (nếu có)

    @CreatedBy
    private ObjectId createdBy;
    @LastModifiedBy
    private ObjectId updatedBy;
    private ObjectId deletedBy;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public TransferTicketStatus getStatusEnum() {
        return status == null ? null : TransferTicketStatus.fromId(status);
    }

    public void setStatusEnum(TransferTicketStatus status) {
        this.status = status == null ? null : status.getId();
    }
}