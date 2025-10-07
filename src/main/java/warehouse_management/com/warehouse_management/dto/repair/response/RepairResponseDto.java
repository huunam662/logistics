package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepairResponseDto {

    private ObjectId id;
    private String repairInventoryItemProductCode; // Mã sản phẩm
    private String repairInventoryItemModel; // Model Xe
    private String repairInventoryItemSerialNumber; // Mã serial
    private String reason; // Ghi chú
    private String status; // Trạng thái
    private List<RepairTransactionResponseDto> repairTransactions; // Phiếu bảo hành của sản phẩm

    private LocalDateTime createdAt; // Ngày yêu cầu
    
    private LocalDateTime completedDate; // Ngày hoàn tất
    
    private LocalDateTime expectedCompletionDate; // Ngày dự kiến hoàn tất
}
