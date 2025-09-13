package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DropPartRequest {
    private String vehicleId;    // ID xe
    private String productCode;
    private String componentType;     // Loại bộ phận: frame, valve, fork, engine, wheel, battery, sideshift, charge
    private BigDecimal actualPrice; // Giá bán thực tế
    private BigDecimal priceR0;     // Giá bán R0
    private BigDecimal priceR1;     // Giá bán R1
}