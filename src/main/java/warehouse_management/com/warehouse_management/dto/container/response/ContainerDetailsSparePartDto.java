package warehouse_management.com.warehouse_management.dto.container.response;

import lombok.Data;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventorySparePartDetailsDto;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ContainerDetailsSparePartDto {
    private BigDecimal totalAmounts;
    private List<InventorySparePartDetailsDto> inventoryItemsSparePart;
}
