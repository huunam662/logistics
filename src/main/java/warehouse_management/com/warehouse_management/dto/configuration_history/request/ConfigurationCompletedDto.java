package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConfigurationCompletedDto {

    private String vehicleId;
    private BigDecimal vehiclePriceR0;
    private BigDecimal vehiclePriceR1;

}
