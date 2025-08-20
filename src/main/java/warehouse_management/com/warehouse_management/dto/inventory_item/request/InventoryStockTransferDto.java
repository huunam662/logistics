package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InventoryStockTransferDto {

    private String ticketId;
    private String originWarehouseId;
    private String destinationWarehouseId;
    private List<InventoryItemTransferDto> inventoryItems = new ArrayList<>();

}
