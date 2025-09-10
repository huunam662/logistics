package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehiclePartSwapRequest {

    private String leftVehicle;
    private String rightVehicle;
    private String partType;

    private VehiclePrice leftPrice;
    private VehiclePrice rightPrice;

    @Data
    public static class VehiclePrice {
        private BigDecimal actualSalePrice;
        private BigDecimal salePriceR0;
        private BigDecimal salePriceR1;
    }
}