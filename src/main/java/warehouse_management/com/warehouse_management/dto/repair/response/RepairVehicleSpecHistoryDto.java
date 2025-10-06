package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigurationHistoryDto;

import java.util.List;

@Data
public class RepairVehicleSpecHistoryDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String inventoryType;   //InventoryType
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String model;          // Model sản phẩm – Bắt buộc
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc

    private List<RepairHistoryDto> repairHistories;

}
