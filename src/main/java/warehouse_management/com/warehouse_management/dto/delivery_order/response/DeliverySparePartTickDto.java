package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;

@Data
public class DeliverySparePartTickDto {
    private String poNumber;
    private String model;
    private String commodityCode;
    private Integer quantity;
}
