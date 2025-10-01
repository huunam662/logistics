package warehouse_management.com.warehouse_management.dto.warehouse_transaction.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateDeliveryTicketDTO {
    private String orderId;
    private List<String> inventoryItemIds;
}
