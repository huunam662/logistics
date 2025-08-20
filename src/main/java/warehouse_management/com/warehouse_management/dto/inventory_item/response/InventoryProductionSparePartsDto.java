package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;        // Ngày đặt hàng
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String description;       // Mô tả
    private Integer quantity;   // Số lượng hàng hóa
    private BigDecimal purchasePrice;       // Giá mua vào
    private BigDecimal actualSalePrice;     // Giá bán thực tế

}
