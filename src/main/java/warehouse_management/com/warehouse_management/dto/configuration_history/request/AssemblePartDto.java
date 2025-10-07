package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssemblePartDto {
    private String vehicleId;
    private BigDecimal vehiclePriceR0;  // Giá xe R0
    private BigDecimal vehiclePriceR1;  // Giá xe R1
    private BigDecimal vehicleOtherPrice; // Giá xe Khác
    private String componentId;
    private String configurationCode;
}
