package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DropPartDto {
    private String vehicleId;    // ID xe
    private BigDecimal vehiclePriceR0;  // Giá xe R0
    private BigDecimal vehiclePriceR1;  // Giá xe R1
    private BigDecimal vehicleOtherPrice; // Giá xe Khác
    private String productCode;
    private String componentType;     // Loại bộ phận: frame, valve, fork, engine, wheel, battery, sideshift, charge
    private BigDecimal priceR0;     // Giá bán R0
    private BigDecimal priceR1;     // Giá bán R1
    private BigDecimal otherPrice;
    private String configurationCode;
}