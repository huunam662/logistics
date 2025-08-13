package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class InventoryTransferDestinationConsignmentDto {
    private String consignmentWarehouseId;
    @Schema(example = "yyyy-MM-dd")
    private String consignmentDate;
    private List<InventoryItemTransfer> inventoryItems = new ArrayList<>();

    @Data
    public static class InventoryItemTransfer{
        String id;
        Integer quantity;
    }
}
