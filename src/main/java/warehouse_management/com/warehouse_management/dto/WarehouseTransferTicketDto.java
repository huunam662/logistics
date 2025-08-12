package warehouse_management.com.warehouse_management.dto;

import lombok.Data;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarehouseTransferTicketDto {
    private ObjectId id; // _id – Khóa chính
    private String status; // PENDING, APPROVED, REJECTED
    private List<ObjectId> inventoryItemIds;    // Id các mặt hàng cần duyệt
    private ObjectId originWarehouseId;     // Kho nguồn
    private String originWarehouseName;
    private String originWarehouseAddress;
    private ObjectId destinationWarehouseId; // Kho đích
    private String destinationWarehouseName;
    private String destinationWarehouseAddress;
    private ObjectId requesterId;               // Người tạo yêu cầu
    private ObjectId approverId;                // Người duyệt hoặc từ chối
    private String rejectReason;              // Lý do từ chối (nếu có)
    private ObjectId createdBy;
    private ObjectId updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
