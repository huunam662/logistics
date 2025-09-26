package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class VehicleConfigurationPageDto {

    private ObjectId id;
    private String configurationCode;   // Mã cấu hình
    private ObjectId vehicleId;
    private String vehicleProductCode;  // Mã sản phẩm (Xe)
    private String vehicleSerial;   // Serial của Xe
    private String vehicleModel;    // Model của Xe
    private String warehouseName;   // Tên kho chứa Xe
    private String componentOldSerial;  // Serial của bộ phận cũ
    private String componentReplaceSerial;  // Serial của bộ phận thay thế
    private String componentType;    // Loại bộ phận
    private String componentName;    // Tên loại bộ phận cũ
    private String configType;    // Kiểu cấu hình thay đổi
    private String description; // Mô tả cấu hình
    private String status;  // Trạng thái phiên thay đổi cấu hình
    
    private LocalDateTime confirmedAt;
    
    private LocalDateTime completedAt;
    
    private LocalDateTime createdAt;
    private String confirmedBy; // người xác nhận
    private String completedBy; // Người hoàn tất
    private String createdBy; // Tên người yêu cầu

}
