package warehouse_management.com.warehouse_management.dto.Inventory.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.model.Warehouse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class InventoryDestinationSparePartsRes {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm hoặc hàng hóa – Bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private Warehouse warehouse; // Kho lưu trữ mặt hàng này
    private InventoryDestinationSparePartsRes.Pricing pricing;               // Giá cả – Không bắt buộc
    private InventoryDestinationSparePartsRes.Logistics logistics;           // Thông tin vận chuyển – Không bắt buộc

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Logistics {
        private LocalDateTime orderDate;        // Ngày đặt hàng
        private LocalDateTime departureDate;    // Ngày khởi hành
        private LocalDateTime arrivalDate;      // Ngày đến
    }
}
