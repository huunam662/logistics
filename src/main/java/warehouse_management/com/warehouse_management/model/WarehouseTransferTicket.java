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

    private String status; // PENDING_APPROVAL, APPROVED, REJECTED, COMPLETED

    private String originWarehouseId;         // ObjectId dạng String – Kho đi
    private String destinationWarehouseId;    // ObjectId dạng String – Kho đến

    private List<TransferItem> items;         // Danh sách sản phẩm

    private String requesterId;               // Người tạo yêu cầu
    private String approverId;                // Người duyệt hoặc từ chối

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


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferItem {
        private String productId;  // ID sản phẩm
        private int quantity;      // Số lượng
    }

    public TransferTicketStatus getStatusEnum() {
        return status == null ? null : TransferTicketStatus.fromId(status);
    }

    public void setStatusEnum(TransferTicketStatus status) {
        this.status = status == null ? null : status.getId();
    }
}