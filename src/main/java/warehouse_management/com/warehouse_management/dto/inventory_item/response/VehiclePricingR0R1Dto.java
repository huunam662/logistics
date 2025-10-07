package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;

@Data
public class VehiclePricingR0R1Dto {

    private ObjectId vehicleId;
    private BigDecimal vehiclePriceR0;  // Giá xe R0
    private BigDecimal vehiclePriceR1;  // Giá xe R1
    private BigDecimal otherPrice;

}
