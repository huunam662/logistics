package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DoNotUseInventoryItem {

    // ================== XePK+PT ==================
    private String poNumber;          // XePK+PT // REQUIRED
    private String notes;             // XePK+PT

    private Pricing pricing;          // XePK+PT
    @Data
    public static class Pricing {
        private BigDecimal purchasePrice;   // XePK+PT
        private BigDecimal salePriceR0;     // XePK+PT
        private BigDecimal salePriceR1;     // XePK+PT
        private BigDecimal actualSalePrice; // XePK+PT

        private String agent;               // XePK // REQUIRED
    }

    // ================== XePK ==================
    private String productCode;       // XePK // REQUIRED
    private String model;             // XePK // REQUIRED
    private String type;              // XePK // REQUIRED
    private String category;          // XePK // REQUIRED
    private String serialNumber;      // XePK // REQUIRED
    private Boolean initialCondition;  // XePK

    private Specifications specifications;  // XePK
    @Data
    public static class Specifications {
        private Integer liftingCapacityKg;   // XePK
        private String chassisType;          // XePK
        private Integer liftingHeightMm;     // XePK
        private String engineType;           // XePK
        private String batteryInfo;          // XePK
        private String batterySpecification; // XePK
        private String chargerSpecification; // XePK
        private String forkDimensions;       // XePK
        private Integer valveCount;          // XePK
        private Boolean hasSideShift;        // XePK
        private String otherDetails;         // XePK
    }

    private Logistics logistics;            // XePK
    @Data
    public static class Logistics {
        private String orderDate;              // XePK // REQUIRED
        private String estimateCompletionDate; // XePK // REQUIRED
    }

    // ================== PT ==================
    private String commodityCode;     // PT
    private Integer quantity;         // PT
    private String description;       // PT
    private String contractNumber;    // PT
}

