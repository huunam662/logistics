package warehouse_management.com.warehouse_management.dto.configuration_vehicle.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VehiclePartSwapRequest {

    private String leftVehicleId;
    private String rightVehicleId;
    private String componentType;

    private VehiclePrice leftPrice;
    private VehiclePrice rightPrice;

    @Data
    public static class VehiclePrice {
        private BigDecimal actualSalePrice;
        private BigDecimal salePriceR0;
        private BigDecimal salePriceR1;
    }
}