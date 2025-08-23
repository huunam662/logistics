package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateDeliveryOrderDto {
    private String deliveryOrderCode; // Mã đơn giao haàng
    private String customerId; // Id khách hàng
    private LocalDateTime deliveryDate; // Ngày giao hàng
    private Integer holdingDays; // Số ngày giữ hàng
}
