package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryItemProductDetails {
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private DeliveryItemProductDetails.Pricing pricing;
    private DeliveryItemProductDetails.Specifications specifications;

    @Data
    public static class Specifications{
        private Integer liftingCapacityKg;      // Sức nâng (kg)
        private Integer liftingHeightMm;        // Độ cao nâng (mm)
        private String engineType;              // Loại động cơ
        private String batteryInfo;             // Thông tin bình điện
        private String batterySpecification;    // Thông số bình điện
    }

    @Data
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal actualSalePrice;     // Giá bán thực tế
        private String agent;                   // Đại lý (nếu có)
    }
}
