package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class ItemCodePriceDto extends VehiclePricingR0R1Dto{

    private String code;
    private BigDecimal salePriceR0;         // Giá bán đề xuất R0
    private BigDecimal salePriceR1;         // Giá bán đề xuất R1

}
