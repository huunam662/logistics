package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DeleteBulkInventoryItemDto(
        @NotEmpty(message = "List of inventory item IDs cannot be empty")
        List<String> inventoryItemIds
) {
}
