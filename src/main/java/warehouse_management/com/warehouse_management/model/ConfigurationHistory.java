package warehouse_management.com.warehouse_management.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "configuration_hist")
@Data
public class ConfigurationHistory {
    @Id
    private ObjectId id;
    private ObjectId vehicleId;
    private String productCode;

    // PK
    private ObjectId liftingFrameId;
    private String liftingFrameLabel;
    private ObjectId prevLiftingFrameId;
    private String prevLiftingFrameLabel;

    private ObjectId batteryId;
    private String batteryLabel;
    private ObjectId prevBatteryId;
    private String prevBatteryLabel;

    private ObjectId chargerId;
    private String chargerLabel;
    private ObjectId prevChargerId;
    private String prevChargerLabel;

    // PT
    private String prevEngineType;        // Loại động cơ
    private String engineType;            // Loại động cơ

    private String prevForkDimensions;    // Thông số càng
    private String forkDimensions;        // Thông số càng

    private Integer prevValveCount;       // Số lượng van
    private Integer valveCount;           // Số lượng van

    private Boolean prevHasSideShift;     // Có side shift không
    private Boolean hasSideShift;         // Có side shift không

    private String note;
    private boolean isLatest;
    private boolean isOrigin;

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
}