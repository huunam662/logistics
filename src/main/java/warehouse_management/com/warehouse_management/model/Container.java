package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import warehouse_management.com.warehouse_management.enumerate.ContainerStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "container")
public class Container {
    @Id
    private ObjectId id;  // _id – Khóa chính

    private String containerCode;       // Mã định danh duy nhất
    private String containerStatus;     // EMPTY, LOADING, IN_TRANSIT, COMPLETED

    private LocalDateTime departureDate;  // Ngày khởi hành
    private LocalDateTime arrivalDate;    // Ngày đến nơi
    private LocalDateTime completionDate; // Ngày hoàn tất

    private List<InventoryItemContainer> inventoryItems;    // Các mặt hàng có trong container

    private String note;                // Ghi chú

    private ObjectId fromWareHouseId;     // Tham chiếu đến _id kho đi
    private ObjectId toWarehouseId;       // Tham chiếu đến _id kho đến

    @CreatedBy
    private ObjectId createdBy;
    @LastModifiedBy
    private ObjectId updatedBy;
    private ObjectId deletedBy;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public ContainerStatus getContainerStatus() {
        return containerStatus == null ? null : ContainerStatus.fromId(containerStatus);
    }

    public void setContainerStatus(ContainerStatus containerStatus) {
        this.containerStatus = containerStatus == null ? null : containerStatus.getId();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryItemContainer{
        private ObjectId id; // _id – Khóa chính
        private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
        private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
        private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
        private String serialNumber;   // Số seri – Có cho xe/phụ kiện
        private String model;          // Model sản phẩm – Bắt buộc
        private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
        private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
        private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
        private Integer quantity;   // Số lượng hàng hóa
        private String contractNumber; // Số hợp đồng
        private String warehouseType;  // Loại kho (kho bảo quản dành cho hàng hóa)
        private Boolean initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
        private String notes;                  // Ghi chú chung – Không bắt buộc
        private String description;         // Mô tả
        private Specifications specifications;
        private Pricing pricing;
        private Logistics logistics;


        @Data
        @NoArgsConstructor
        @AllArgsConstructor
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

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Pricing {
            @Field(targetType = FieldType.DECIMAL128)
            private BigDecimal purchasePrice;       // Giá mua vào
            private String agent;                   // Đại lý (nếu có)
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Logistics {
            private LocalDateTime orderDate;        // Ngày đặt hàng
            private LocalDateTime departureDate;    // Ngày khởi hành
            private LocalDateTime arrivalDate;      // Ngày đến
        }
    }
}