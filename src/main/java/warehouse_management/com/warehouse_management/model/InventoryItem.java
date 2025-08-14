package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse_management.com.warehouse_management.enumerate.InventoryItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inventory_item")
public class InventoryItem {
    @Id
    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB

    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String commodityCode;  // Mã hàng hóa (đôi với phụ tùng)
    private String serialNumber;   // Số seri – Có cho xe/phụ kiện
    private String model;          // Model sản phẩm – Bắt buộc
    private String type;           // Loại sản phẩm (VD: Xe nâng điện) – Bắt buộc
    private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private Integer quantity;   // Số lượng hàng hóa
    private String status;         // Trạng thái hiện tại (IN_STOCK, IN_TRANSIT...) – Bắt buộc
    private String contractNumber; // Số hợp đồng
    private String warehouseType;  // Loại kho (kho bảo quản dành cho hàng hóa)
    private ObjectId warehouseId;  // _id của warehouse – Có nếu đang ở kho
    private ObjectId containerId;  // _id của container – Có nếu đang trong container

    private Specifications specifications; // Thông số kỹ thuật – Không bắt buộc
    private Pricing pricing;               // Giá cả – Không bắt buộc
    private Logistics logistics;           // Thông tin vận chuyển – Không bắt buộc

    private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String description;         // Mô tả

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

    // --- Inner Classes ---

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
        private String ForkDimensions;          // Thông số càng
        private Integer valveCount;             // Số lượng van
        private Boolean hasSideShift;           // Có side shift không
        private String otherDetails;            // Chi tiết khác
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
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

    public InventoryItemStatus getStatus() {
        return status == null ? null : InventoryItemStatus.fromId(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(InventoryItemStatus inventoryItemStatus) {
        this.status = inventoryItemStatus == null ? null : inventoryItemStatus.getId();
    }
}

