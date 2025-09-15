package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeliverySparePartDetailsDto {

    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String model;          // Model sản phẩm – Bắt buộc
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String contractNumber; // Số hợp đồng
    private Integer quantity = 1;   // Số lượng hàng hóa
    private ObjectId warehouseId;
    private String description;         // Mô tả
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private Boolean isDelivered;    // Đã hoặc chưa giao
    private Pricing pricing;
    private Logistics logistics;

    @Data
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal actualSalePrice;     // Giá bán thực tế
    }

    @Data
    public static class Logistics {
        private LocalDateTime orderDate;        // Ngày đặt hàng
    }
}
