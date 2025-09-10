package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

@Data
public class InventoryItemModelDto {
    private String warehouseId;
    private String warehouseType;
    private String model;
    private String inventoryItemId;
    private String productCode;
    private String commodityCode;
    private Integer quantity;


    private Integer liftingCapacityKg;      // Sức nâng (kg)
    private String chassisType;             // Loại khung nâng
    private Integer liftingHeightMm;        // Độ cao nâng (mm)

    private String batteryInfo;             // Thông tin bình điện
    private String batterySpecification;    // Thông số bình điện

    private String chargerSpecification;    // Thông số bộ sạc

    private String engineType;              // Loại động cơ
    private String forkDimensions;          // Thông số càng
    private Integer valveCount;             // Số lượng van
    private Boolean hasSideShift;           // Có side shift không
    private String otherDetails;            // Chi tiết khác

}
