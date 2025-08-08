package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class InventoryTransferWarehouseReq {

    private String toWarehouseId;
    @Schema(example = "yyyy-MM-dd")
    private String arrivalDate;
    private List<InventoryItemTransfer> inventoryItems = new ArrayList<>();

    @Data
    @NoArgsConstructor
    public static class InventoryItemTransfer{
        String id;
        Integer quantity;
    }

}
