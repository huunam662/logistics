package warehouse_management.com.warehouse_management.dto.container.response;

import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryProductDetailsDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventorySparePartDetailsDto;
import java.util.List;

@Data
public class ContainerItemsDetailsDto {
    private ObjectId containerId;
    private String containerCode;
    private List<InventoryProductDetailsDto> products;
    private List<InventorySparePartDetailsDto> spareParts;
}
