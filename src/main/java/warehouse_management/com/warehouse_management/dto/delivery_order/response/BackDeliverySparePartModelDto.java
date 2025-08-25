package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;

@Data
public class BackDeliverySparePartModelDto {
    private String model;
    private Integer quantity;
    private String description;
}
