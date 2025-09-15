package warehouse_management.com.warehouse_management.dto.warranty.request;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CreateWarrantyTransactionDTO {
    private ObjectId warrantyTransactionId;     // Id phiếu bảo hành
    private ObjectId warrantyId;                // Đơn bảo hành cha
    private String sparePartWarranty;           // Bộ phận cần bảo hành
    private String reason;                      // Lý do bảo hành
    private Boolean isCompleted;                // Trạng thái hoàn thành của phiếu
}
