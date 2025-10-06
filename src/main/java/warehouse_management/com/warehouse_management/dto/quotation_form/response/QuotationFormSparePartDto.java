package warehouse_management.com.warehouse_management.dto.quotation_form.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

@Data
public class QuotationFormSparePartDto {

    private ObjectId id;
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private String model;
    private String description;
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String contractNumber; // Số hợp đồng
    private BigDecimal salePrice;
    private String quotationType;
}
