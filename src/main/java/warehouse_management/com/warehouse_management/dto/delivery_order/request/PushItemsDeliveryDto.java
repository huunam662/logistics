package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;

import java.util.List;

@Data
public class PushItemsDeliveryDto {
    private String deliveryOrderId;
    private List<PushItemToDeliveryDto> inventoryItemsDelivery;
}
