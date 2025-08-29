package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeliveryProductDetailsDto {

    private String model;          // Model sản phẩm – Bắt buộc
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String warehouseType;  // Loại kho (kho bảo quản dành cho hàng hóa)
    private Boolean initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private Boolean isDelivered;    // Đã hoặc chưa giao
    private Specifications specifications;
    private Pricing pricing;

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

}
