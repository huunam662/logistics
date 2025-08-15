package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryProductDetailsDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String model;          // Model sản phẩm – Bắt buộc
    private String category; // Ví dụ: Reach Truck, Pallet Truck...7
    private String serialNumber; // Số series nhà máy 8
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String type;           // Loại sản phẩm (VD: Xe nâng điện) – Bắt buộc
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private Specifications specifications;
    private Pricing pricing;
    private Logistics logistics;


    @Data
    public static class Specifications {
        private Integer liftingCapacityKg;      // Sức nâng (kg)
        private String chassisType;             // Loại khung nâng
        private Integer liftingHeightMm;        // Độ cao nâng (mm)
        private String engineType;              // Loại động cơ
        private String batteryInfo;             // Thông tin bình điện
        private String batterySpecification;    // Thông số bình điện
        private String chargerSpecification;    // Thông số bộ sạc
        private String forkDimensions;          // Thông số càng
        private Integer valveCount;             // Số lượng van
        private Boolean hasSideShift;           // Có side shift không
        private String otherDetails;            // Chi tiết khác
    }

    @Data
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal actualSalePrice;     // Giá bán thực tế
        private String agent;                   // Đại lý (nếu có)
    }

    @Data
    public static class Logistics {
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime orderDate; // Ngày đặt hàng 2
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime estimateCompletionDate; // Chỉ dùng trong báo cáo hàng chờ SX 27
    }
}
