package warehouse_management.com.warehouse_management.dto.warranty.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class WarrantyResponseDTO {
    private ObjectId id;
    private String warrantyInventoryItemProductCode; // Mã sản phẩm
    private String warrantyInventoryItemModel; // Model Xe
    private String warrantyInventoryItemSerialNumber; // Mã serial
    private String clientName; // Tên khách hàng
    private String note; // Ghi chú
    private String status; // Trạng thái
    private List<WarrantyTransactionResponseDTO> warrantyTransactions; // Phiếu bảo hành của sản phẩm

    
    private LocalDateTime arrivalDate; // Ngày giao hàng

    
    private LocalDateTime createdAt; // Ngày bảo hành

    
    private LocalDateTime completedDate;
}
