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
@Document(collection = "quotation_form")
public class QuotationForm implements Persistable<ObjectId> {

    @Id
    private ObjectId id;
    private String quotationCode;
    private String customerId;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerEmail;
    private String customerLevel;

    private List<InventoryItemQuotation> quotationInventoryItems;

    // ===REF===
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;
    private String deletedBy;

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
    public static class InventoryItemQuotation{

        private ObjectId id;
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
        private ObjectId warehouseId;  // _id của warehouse – Có nếu đang ở kho
        private String quotationType;

        @Field(targetType = FieldType.DECIMAL128)
        private BigDecimal salePrice;         // Giá bán đề xuất theo cấp độ của khách hàng
        private String agent;

        private Specifications specifications;

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
    }
}
