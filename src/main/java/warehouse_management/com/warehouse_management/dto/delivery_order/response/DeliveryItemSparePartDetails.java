package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DeliveryItemSparePartDetails {
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String model;          // Model sản phẩm – Bắt buộc
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String contractNumber; // Số hợp đồng
    private Integer quantity;   // Số lượng hàng hóa
    private String notes;                  // Ghi chú chung – Không bắt buộc

    @Data
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal actualSalePrice;     // Giá bán thực tế
    }
}
