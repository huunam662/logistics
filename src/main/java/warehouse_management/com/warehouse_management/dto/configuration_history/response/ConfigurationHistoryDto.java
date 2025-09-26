package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class ConfigurationHistoryDto {

    private ObjectId id;

    private ObjectId vehicleId;

    private ObjectId componentOldId; // Id của bộ phận cũ
    private String componentOldSerial;  // Serial của bộ phận mới

    private ObjectId componentReplaceId;    // Id của bộ phận được thay thế
    private String componentReplaceSerial;  // Serial của bộ phận được thay thế

    private String componentType;    // Loại bộ phận cũ
    private String componentName;    // Tên loại bộ phận cũ

    private String configType;    // Kiểu cấu hình thay đổi

    private String description;

    private String performedBy; // Tên người thao tác

    
    private LocalDateTime performedAt;


}
