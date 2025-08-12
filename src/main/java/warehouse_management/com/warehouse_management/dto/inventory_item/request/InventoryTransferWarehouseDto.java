package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InventoryTransferWarehouseDto {

    private String productionWarehouseId;
    private String departureWarehouseId;
    @Schema(example = "yyyy-MM-dd")
    private String arrivalDate;
    private List<InventoryItemTransfer> inventoryItems = new ArrayList<>();

    @Data
    public static class InventoryItemTransfer{
        String id;
        Integer quantity;
    }

}
