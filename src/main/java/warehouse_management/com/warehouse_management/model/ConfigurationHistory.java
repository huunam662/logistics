package warehouse_management.com.warehouse_management.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "configuration_hist")
@Data
public class ConfigurationHistory implements Persistable<ObjectId> {

    @Id
    private ObjectId id;

    private ObjectId vehicleId;

    private ObjectId componentOldId; // Id của bộ phận cũ
    private String componentOldSerial;  // Serial của bộ phận mới

    private ObjectId componentReplaceId;    // Id của bộ phận được thay thế
    private String componentReplaceSerial;  // Serial của bộ phận được thay thế

    private String componentType;    // Loại bộ phận cũ

    private String configType;    // Kiểu cấu hình thay đổi

    private String description;

    private String performedBy; // Tên người thao tác

    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    private ObjectId deletedBy;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    @Override
    public boolean isNew() {
        return createdAt == null;
    }
}