package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import warehouse_management.com.warehouse_management.model.DeliveryOrder;
import java.util.ArrayList;
import java.util.List;

@Data
public class DeliveryOrderItemsDto {
    private List<DeliveryItemModelDto> items = new ArrayList<>();
    private List<DeliveryOrder.BackDeliveryModel> modelNotes = new ArrayList<>();
}
