package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.bson.types.ObjectId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class InventoryItemCreateDto {

    private String poNumber;

    private String productCode;

    private String serialNumber;

    private String model;

    private String type;

    private String category;

    private String inventoryType;

    private Integer quantity;

    @NotBlank(message = "ID kho là bắt buộc")
    private String warehouseId;

    @Valid
    private SpecificationsDto specifications;

    @Valid
    private PricingDto pricing;

    @Valid
    private LogisticsDto logistics;

    private String initialCondition;
    private String notes;

    @Data
    public static class SpecificationsDto {
        private Integer liftingCapacityKg;
        private String chassisType;
        private Integer liftingHeightMm;
    }

    @Data
    public static class PricingDto {
        private BigDecimal purchasePrice;
        private BigDecimal salePriceR0;
    }

    @Data
    public static class LogisticsDto {
        private LocalDateTime orderDate;
        private LocalDateTime departureDate;
    }
}