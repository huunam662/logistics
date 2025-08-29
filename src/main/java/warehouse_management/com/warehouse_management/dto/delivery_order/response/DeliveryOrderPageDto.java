package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DeliveryOrderPageDto {
    private ObjectId id;    // Khóa chính
    private String deliveryOrderCode;   // Mã đơn giao hàng (tự sinh hoặc nhập tay).
    private String customerId;      // Mã khách hàng
    private String customerName;    // Tên khách hàng
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // Ngày tạo
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryDate;  // Ngày giao hàng (hỗ trợ chọn quá khứ).
    private Integer holdingDays;    // Số ngày giữ hàng (số nguyên dương).
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime holdingDeadlineDate;  // Hạn giữ hàng
    private String status; // Trạng thái đơn giao hàng
    private BigDecimal totalPurchasePrice;       // Giá mua vào
    private BigDecimal totalActualSalePrice;     // Giá bán thực tế
}
