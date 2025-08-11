package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse_management.com.warehouse_management.enumerate.TransferTicketStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "warehouse_transfer_ticket")
public class WarehouseTransferTicket {

    @Id
    private ObjectId id; // _id – Khóa chính

    private String status; // PENDING, APPROVED, REJECTED

    private String originWarehouseId;         // ObjectId dạng String – Kho đi
    private String destinationWarehouseId;    // ObjectId dạng String – Kho đến
    private List<ObjectId> inventoryItemIds;

    private ObjectId requesterId;               // Người tạo yêu cầu
    private ObjectId approverId;                // Người duyệt hoặc từ chối

    private String notes;                     // Ghi chú
    private String rejectReason;              // Lý do từ chối (nếu có)

    private LocalDateTime processedAt;        // Thời gian xử lý (duyệt/từ chối)

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