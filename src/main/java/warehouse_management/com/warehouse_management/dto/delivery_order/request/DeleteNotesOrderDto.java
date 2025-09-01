package warehouse_management.com.warehouse_management.dto.delivery_order.request;

import lombok.Data;

import java.util.List;

@Data
public class DeleteNotesOrderDto {
    private String deliveryOrderId;
    private List<String> models;
}
