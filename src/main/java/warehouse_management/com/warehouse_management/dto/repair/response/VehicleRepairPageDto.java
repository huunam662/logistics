package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class VehicleRepairPageDto {

    private ObjectId id;
    private String repairCode;   // Mã cấu hình
    private ObjectId vehicleId;
    private String vehicleProductCode;  // Mã sản phẩm (Xe)
    private String vehicleSerial;   // Serial của Xe
    private String vehicleModel;    // Model của Xe
    private String warehouseName;   // Tên kho chứa Xe
    private String componentSerial;  // Serial của bộ phận cũ
    private String componentType;    // Loại bộ phận
    private String componentName;    // Tên loại bộ phận cũ
    private String repairType;    // Kiểu cấu hình thay đổi
    private String description; // Mô tả cấu hình
    private String status;  // Trạng thái phiên thay đổi cấu hình

    private LocalDateTime confirmedAt;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
    private String confirmedBy; // người xác nhận
    private String completedBy; // Người hoàn tất
    private String createdBy; // Tên người yêu cầu
}
