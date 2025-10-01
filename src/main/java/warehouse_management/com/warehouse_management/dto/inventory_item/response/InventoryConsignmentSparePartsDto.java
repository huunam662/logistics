package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryConsignmentSparePartsDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String warehouseName;       // Tên kho (bắt buộc)
    private String contractNumber;      // Số hợp đồng
    
    private LocalDateTime orderDate;        // Ngày đặt hàng
    
    private LocalDateTime consignmentDate;  // Ngày ký gửi (nếu có)
    private String model;          // Model sản phẩm – Bắt buộc
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String description;       // Mô tả
    private Integer quantity;   // Số lượng hàng hóa
    private BigDecimal purchasePrice;       // Giá mua vào
    private BigDecimal salePriceR0;         // Giá bán đề xuất R0
    private BigDecimal salePriceR1;         // Giá bán đề xuất R1
    private BigDecimal otherPrice;      // Giá khác

}
