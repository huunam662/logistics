package warehouse_management.com.warehouse_management.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Document(collection = "configuration_hist")
@Data
public class ConfigurationHistory implements Persistable<ObjectId> {

    @Id
    private ObjectId id;

    private ObjectId vehicleId;

    private String configurationCode;

    private ObjectId componentOldId; // Id của bộ phận cũ
    private String componentOldSerial;  // Serial của bộ phận mới

    private ObjectId componentReplaceId;    // Id của bộ phận được thay thế
    private String componentReplaceSerial;  // Serial của bộ phận được thay thế

    private String componentType;    // Loại bộ phận cũ

    private String configType;    // Kiểu cấu hình thay đổi

    private String description;

    private String status;  // Trạng thái phiên thay đổi cấu hình

    private LocalDateTime confirmedAt;

    private LocalDateTime completedAt;

    private LocalDateTime performedAt;

    private String confirmedBy;

    private String completedBy; // Người trực tiếp (hoán đổi, tháo rời, lắp ráp)

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

    public ConfigurationHistory() {
        this.configurationCode = "CVH-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "-" + ThreadLocalRandom.current().nextInt(10000, 100000);
    }
}