package warehouse_management.com.warehouse_management.dto.repair.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepairResponseDTO {
    private ObjectId id;
    private String repairInventoryItemProductCode; // Mã sản phẩm
    private String repairInventoryItemModel; // Model Xe
    private String repairInventoryItemSerialNumber; // Mã serial
    private String note; // Ghi chú
    private String status; // Trạng thái
    private List<RepairTransactionResponseDTO> repairTransactions; // Phiếu bảo hành của sản phẩm

    
    private LocalDateTime createdAt; // Ngày bảo hành

    
    private LocalDateTime completedDate; // Ngày hoàn tất

    
    private LocalDateTime expectedCompletionDate; // Ngày dự kiến hoàn tất
}
