package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventorySparePartDetailsDto {
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private LocalDateTime orderDate;        // Ngày đặt hàng
    private String model;
    private String description;
    private ObjectId warehouseId; // Mã kho
    private String warehouseCode;
    private String warehouseName;
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String contractNumber; // Số hợp đồng
    private Pricing pricing;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal actualSalePrice;
        private BigDecimal otherPrice;      // Giá khác
    }
}
