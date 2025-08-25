package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;

@Data
public class PushItemToDeliveryDto {
    private String id;
    private Boolean isDelivered;
    private Integer quantity;
    private String model;
    private Boolean isSparePart;
}
