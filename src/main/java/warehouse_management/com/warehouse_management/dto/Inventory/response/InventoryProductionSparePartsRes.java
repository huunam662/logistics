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
public class InventoryProductionSparePartsRes {
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm hoặc hàng hóa – Bắt buộc
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private Warehouse warehouse; // Kho lưu trữ mặt hàng này
    private InventoryProductionSparePartsRes.Pricing pricing;               // Giá cả – Không bắt buộc
    private InventoryProductionSparePartsRes.Logistics logistics;           // Thông tin vận chuyển – Không bắt buộc


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal actualSalePrice;     // Giá bán thực tế
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Logistics {
        private LocalDateTime orderDate;        // Ngày đặt hàng
        private LocalDateTime plannedProductionDate; // Ngày dự kiến sản xuất
        private LocalDateTime estimateCompletionDate; // Ngày dự kiến sản xuất xong
    }
}
