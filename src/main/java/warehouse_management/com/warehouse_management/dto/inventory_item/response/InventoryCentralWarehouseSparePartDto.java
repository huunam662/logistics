package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryCentralWarehouseSparePartDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private String model;   // Model
    private String description; // Mô tả
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String contractNumber; // Số hợp đồng
    private String warehouseType;   // Loại kho
    private BigDecimal purchasePrice;       // Giá mua vào
    private BigDecimal salePriceR0;         // Giá bán đề xuất R0
    private BigDecimal salePriceR1;         // Giá bán đề xuất R1
    private BigDecimal actualSalePrice; // Giá bán thực tế
    private String agent;                   // Đại lý (nếu có)
    private String warehouseName;           // Tên kho
    private String warehouseCode;           // Mã kho
}
