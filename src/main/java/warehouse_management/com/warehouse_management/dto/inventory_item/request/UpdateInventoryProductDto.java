package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class UpdateInventoryProductDto extends CreateInventoryProductDto {

    @NotNull(message = "Inventory item id is required.")
    String id;

}
