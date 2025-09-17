package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "delivery_order")
public class DeliveryOrder implements Persistable<ObjectId> {

    @Id
    private ObjectId id; // _id Khóa chính

    private String deliveryOrderCode;   // Mã đơn giao hàng (tự sinh hoặc nhập tay).

    private ObjectId customerId;    // Mã khách hàng (chọn từ danh sách tài khoản).

    private LocalDateTime deliveryDate;  // Ngày giao hàng (hỗ trợ chọn quá khứ).

    private Integer overdueDays;    // Số ngày quá hạn

    private Integer holdingDays;    // Số ngày giữ hàng (số nguyên dương).

    private String status; // Trạng thái đơn giao hàng

    private List<InventoryItemDelivery> inventoryItems; // Các mặt hàng trong đơn giao

    private List<NoteDeliveryModel> modelNotes;  // Các Model còn nợ
    private List<DeliveryHistory> deliveryHistories; // Lịch sử thao tác đơn hàng

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

    @Override
    public boolean isNew() {
        return createdAt == null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteDeliveryModel {
        private String model; // Model sản phẩm
        private Boolean isSparePart;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryItemDelivery{
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
        private Boolean isDelivered;    // Đã hoặc chưa giao
        private ObjectId warehouseId;  // _id của warehouse – Có nếu đang ở kho
        private String itemDeliveryStatus;          // Trạng thái sản phẩm trong đơn
        private InventoryItemDelivery.Specifications specifications;
        private InventoryItemDelivery.Pricing pricing;
        private InventoryItemDelivery.Logistics logistics;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Specifications {
            private String liftingCapacityKg;      // Sức nâng (kg)
            private String chassisType;             // Loại khung nâng
            private String liftingHeightMm;        // Độ cao nâng (mm)
            private String engineType;              // Loại động cơ
            private String batteryInfo;             // Thông tin bình điện
            private String batterySpecification;    // Thông số bình điện
            private String chargerSpecification;    // Thông số bộ sạc
            private String forkDimensions;          // Thông số càng
            private String valveCount;             // Số lượng van
            private String hasSideShift;           // Có side shift không
            private String otherDetails;            // Chi tiết khác
        }

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
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryHistory {
        private ObjectId id;
        private String action; // "ADD_ITEM", "REMOVE_ITEM", "UPDATE_ITEM"
        private String productCode;
        private String commodityCode;
        private String model;
        private Integer originalQuantity;
        private Integer newQuantity;
        private String reason;
        private String performedBy;
        private LocalDateTime performedAt;
        private Boolean isSparePart;
    }
}
