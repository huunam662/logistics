package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.ItemCodePriceDto;

@Data
public class SwapVehiclePricingDto {

    private ItemCodePriceDto vehicleLeftPricing;

    private ItemCodePriceDto vehicleRightPricing;

}
