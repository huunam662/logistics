package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InventoryTransferConsignmentDestinationDto {

    private String destinationWarehouseId;
    private List<InventoryItemTransferDto> inventoryItems = new ArrayList<>();

}
