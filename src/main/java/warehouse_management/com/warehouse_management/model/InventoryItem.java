package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.index.Indexed;
import warehouse_management.com.warehouse_management.enumerate.AccessoryType;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;
import warehouse_management.com.warehouse_management.enumerate.SparePartType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory_item")
public class InventoryItem {
    @Id
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String inventoryType;   //inventoryType
    private String accessoryType; // AccessoryType
    private String sparePartType; // SparePartType
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc

    //   XE/PK
    @Indexed(unique = true)
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String model;          // Model sản phẩm – Bắt buộc
    //
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc
    private String contractNumber; // Số hợp đồng

    private Boolean initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String otherDetails;

    // --- PK PT ---
    private ObjectId vehicleId; // PK/PT không dùng
    //PK-KN
    private Integer liftingCapacityKg;      // Sức nâng (kg)
    private String chassisType;             // Loại khung nâng
    private Integer liftingHeightMm;        // Độ cao nâng (mm)
    //PK-BINHDIEN
    private String batteryInfo;             // Thông tin bình điện
    private String batterySpecification;    // Thông số bình điện

    //PK-SAC
    private String chargerSpecification;    // Thông số bộ sạc

    //PT
    @Indexed(unique = true)
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String description;         // Mô tả
    private Integer quantity;   // Số lượng hàng hóa
    private String engineType;              // Loại động cơ
    private String forkDimensions;          // Thông số càng
    private Integer valveCount;             // Số lượng van
    private Boolean hasSideShift;           // Có side shift không

    //===PK PT===
    private Pricing pricing;               // Giá cả – Không bắt buộc

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

    private Logistics logistics;           // Thông tin vận chuyển – Không bắt buộc

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Logistics {
        private LocalDateTime orderDate;        // Ngày đặt hàng
        private LocalDateTime departureDate;    // Ngày khởi hành
        private LocalDateTime arrivalDate;      // Ngày đến
        private LocalDateTime consignmentDate;  // Ngày ký gửi (nếu có)
        private LocalDateTime plannedProductionDate; // Ngày dự kiến sản xuất
        private LocalDateTime estimateCompletionDate; // Ngày dự kiến sản xuất xong
    }

    //    REF
    private String warehouseType;  // Loại kho (kho bảo quản dành cho hàng hóa)
    private ObjectId warehouseId;  // _id của warehouse – Có nếu đang ở kho
    private ObjectId containerId;  // _id của container – Có nếu đang trong container
    // ===REF===
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    private ObjectId deletedBy;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public InventoryItemStatus getStatus() {
        return status == null ? null : InventoryItemStatus.fromId(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(InventoryItemStatus inventoryItemStatus) {
        this.status = inventoryItemStatus == null ? null : inventoryItemStatus.getId();
    }

    public void setSparePartType(String sparePartType) {
        this.sparePartType = sparePartType;
    }
    public void setAccessoryType(String accessoryType) {
        this.accessoryType = accessoryType;
    }

    public void setSparePartType(SparePartType sparePartType) {
        this.sparePartType = sparePartType.getId();
    }
    public void setAccessoryType(AccessoryType accessoryType) {
        this.accessoryType = accessoryType.getId();
    }
}

