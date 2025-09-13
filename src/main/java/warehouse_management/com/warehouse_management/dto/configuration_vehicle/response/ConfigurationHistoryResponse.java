package warehouse_management.com.warehouse_management.dto.configuration_vehicle.response;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;

import java.time.LocalDateTime;

public class ConfigurationHistoryResponse {
    @Id
    private ObjectId id;
    private ObjectId vehicleId;

    // PK
    private ObjectId liftingFrameId;
    private String liftingFrameLabel;
    private ObjectId prevLiftingFrameId;
    private ObjectId prevLiftingFrameLabel;

    private ObjectId batteryId;
    private ObjectId batteryLabel;
    private ObjectId prevBatteryId;
    private ObjectId prevBatteryLabel;

    private ObjectId chargerId;
    private ObjectId chargerLabel;
    private ObjectId prevChargerId;
    private ObjectId prevChargerLabel;

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
