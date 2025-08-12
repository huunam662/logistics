package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InventoryStockTransferDto {

    private final String originWarehouseId;
    private final String destinationWarehouseId;
    private List<InventoryItemTransfer> inventoryItems = new ArrayList<>();

    @Data
    public static class InventoryItemTransfer{
        String id;
        Integer quantity;
    }
}
