package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import warehouse_management.com.warehouse_management.enumerate.TransferTicketStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "warehouse_transfer_ticket")
public class WarehouseTransferTicket {

    @Id
    private ObjectId id; // _id – Khóa chính
    private String title;   // Tiêu đề
    private String reason;  // Lý do tạo phiếu
    private String ticketCode;      // Số phiếu điều chuyển (không bắt buộc)

    private String status; // PENDING, APPROVED, REJECTED

    private ObjectId originWarehouseId;     // Kho nguồn
    private ObjectId destinationWarehouseId; // Kho đích

    private ObjectId requesterId;               // Người tạo yêu cầu
    private ObjectId approverId;                // Người duyệt hoặc từ chối

    private List<InventoryItemTicket> inventoryItems;    // Các mặt hàng có trong phiếu

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
    private String jsonPrint;

    private LocalDateTime approvedAt;   // Ngày duyệt

    private Department stockInDepartment;   // Bộ phận nhập kho
    private Department stockOutDepartment;  // Bộ phận xuất kho
    private ShipUnitInfo shipUnitInfo;  //  Thông tin vận chuyển

    public TransferTicketStatus getStatusEnum() {
        return status == null ? null : TransferTicketStatus.fromId(status);
    }

    public void setStatusEnum(TransferTicketStatus status) {
        this.status = status == null ? null : status.getId();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipUnitInfo{
        private String fullName;    // Họ tên
        private String licensePlate;    // Biển số xe
        private String phone;   // Số điện thoại
        private String identityCode;    // Căn cước công dân
        private String reason;  //  Lý do điều chuyển
        private String shipMethod;  // Phương thưc vận chuyển
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Department{
        private String name; // Tên bộ phận
        private String address; // Địa chỉ
        private String phone;   // Số điện thoại
        private String position;    // Chức vụ / vị trí công tác
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryItemTicket{
        private ObjectId id; // _id – Khóa chính
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
        private String initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
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
            private LocalDateTime estimateCompletionDate; // Ngày dự kiến sản xuất xong
        }
    }
}