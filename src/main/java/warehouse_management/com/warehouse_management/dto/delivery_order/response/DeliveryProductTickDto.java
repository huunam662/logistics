package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;

@Data
public class DeliveryProductTickDto {
    private String poNumber;
    private String model;
    private String productCode;
    private Integer quantity;
}
