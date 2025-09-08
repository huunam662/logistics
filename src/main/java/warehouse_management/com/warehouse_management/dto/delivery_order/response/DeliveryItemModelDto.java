package warehouse_management.com.warehouse_management.dto.delivery_order.response;

import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemModelDto;

@Data
public class DeliveryItemModelDto {
    private ObjectId warehouseId;
    private String warehouseType;
    private String model;
    private ObjectId inventoryItemId;
    private String productCode;
    private String commodityCode;
    private Integer quantity;
    private Boolean isDelivered;
    private InventoryItemModelDto.Specifications specifications;

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
        private Integer valveCount;             // Số lượng van
        private Boolean hasSideShift;           // Có side shift không
        private String otherDetails;            // Chi tiết khác
    }
}
