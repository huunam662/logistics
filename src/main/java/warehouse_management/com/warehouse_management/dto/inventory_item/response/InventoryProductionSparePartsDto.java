package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryProductionSparePartsDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String model;          // Model sản phẩm – Bắt buộc
    private LocalDateTime orderDate;        // Ngày đặt hàng
    private String productCode;    // Mã định danh của sản phẩm hoặc hàng hóa – Bắt buộc
    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private BigDecimal purchasePrice;       // Giá mua vào
    private BigDecimal actualSalePrice;     // Giá bán thực tế

}
