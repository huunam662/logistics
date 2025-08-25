package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;

@Data
public class BackDeliveryProductModelDto {
    private String model;
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private Integer quantity;
}
