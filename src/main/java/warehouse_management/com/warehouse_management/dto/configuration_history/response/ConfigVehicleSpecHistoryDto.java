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
        private String liftingFrame;      // Sức nâng (kg)
        //PK-BINHDIEN
        private String battery;    // Thông số bình điện

        //PK-SAC
        private String charger;    // Thông số bộ sạc


        private String engine;              // Loại động cơ
        private String fork;          // Thông số càng
        private String valve;             // Số lượng van
        private String sideShift;           // Có side shift không
    }
}
