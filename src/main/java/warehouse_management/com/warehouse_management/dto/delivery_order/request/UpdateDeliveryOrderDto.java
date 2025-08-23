package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateDeliveryOrderDto extends CreateDeliveryOrderDto{
    private String status;
}
