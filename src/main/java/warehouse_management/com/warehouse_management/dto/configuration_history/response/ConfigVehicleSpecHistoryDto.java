package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class ConfigVehicleSpecHistoryDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String inventoryType;   //InventoryType
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String model;          // Model sản phẩm – Bắt buộc
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc

    private Specifications specificationsBase;

    private List<ConfigurationHistoryDto> configHistories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Specifications {
        //PK-KN
        private String liftingCapacityKg;      // Sức nâng (kg)
        private String chassisType;             // Loại khung nâng
        private String liftingHeightMm;        // Độ cao nâng (mm)
        //PK-BINHDIEN
        private String batteryInfo;             // Thông tin bình điện
        private String batterySpecification;    // Thông số bình điện

        //PK-SAC
        private String chargerSpecification;    // Thông số bộ sạc


        private String engineType;              // Loại động cơ
        private String forkDimensions;          // Thông số càng
        private Integer valveCount;             // Số lượng van
        private Boolean hasSideShift;           // Có side shift không
        private String otherDetails;
    }
}
