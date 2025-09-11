package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CreateRepairTransactionDTO {
    private ObjectId repairId;          // Đơn sửa chữa cha
    private String sparePartWarranty;   // Bộ phận cần sửa chữa
    private String reason;              // Lý do sửa chữa
}
