package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;

import java.util.List;

@Data
public class PushNotesOrderDto {
    private String deliveryOrderId;
    private List<DeliveryOrder.NoteDeliveryModel> notes;
}
