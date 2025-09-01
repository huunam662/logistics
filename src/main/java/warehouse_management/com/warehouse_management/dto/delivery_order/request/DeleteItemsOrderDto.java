package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;

import java.util.List;

@Data
public class DeleteItemsOrderDto {
    private String deliveryOrderId;
    private List<String> itemIds;
}
