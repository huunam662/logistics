package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import warehouse_management.com.warehouse_management.model.InventoryItem;

import java.math.BigDecimal;

@Data
public class InventoryItemModelDto {
    private String warehouseId;
    private String warehouseType;
    private String model;
    private String inventoryItemId;
    private String productCode;
    private String commodityCode;
    private Integer quantity;
    private Specifications specifications;

    @Data
    public static class Specifications {
        private Integer liftingCapacityKg;      // Sức nâng (kg)
        private String chassisType;             // Loại khung nâng
        private Integer liftingHeightMm;        // Độ cao nâng (mm)
        private String engineType;              // Loại động cơ
        private String batteryInfo;             // Thông tin bình điện
        private String batterySpecification;    // Thông số bình điện
        private String chargerSpecification;    // Thông số bộ sạc
        private String forkDimensions;          // Thông số càng
        private String valveCount;             // Số lượng van
        private String hasSideShift;           // Có side shift không
        private String otherDetails;            // Chi tiết khác
    }


    private InventoryItem.Pricing pricing;               // Giá cả – Không bắt buộc

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal purchasePrice;       // Giá mua vào
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal actualSalePrice;     // Giá bán thực tế
        private String agent;                   // Đại lý (nếu có)
    }
}
