package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;

import java.util.List;

@Data
public class InventoryItemToContainerDto {
    private String containerId;
    private List<InventoryItemTransfer> inventoryItems;
    @Data
    public static class InventoryItemTransfer{
        String id;
        Integer quantity;
    }
}
