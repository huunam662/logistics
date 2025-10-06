package warehouse_management.com.warehouse_management.dto.quotation_form.response;

import lombok.Data;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryProductDetailsDto;

import java.math.BigDecimal;

@Data
public class QuotationProductWarehouseDto {

    private ObjectId id; // _id – Khóa chính tự động tạo bởi MongoDB
    private String model;          // Model sản phẩm – Bắt buộc
    private String category; // Ví dụ: Reach Truck, Pallet Truck...7
    private String serialNumber; // Số series nhà máy 8
    private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
    private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
    private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
    private Boolean initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private ObjectId warehouseId; // Mã kho
    private String warehouseCode;
    private String warehouseName;
    private Integer customerLevel;
    private Specifications specifications;
    private Pricing pricing;

    @Data
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
    public static class Pricing {
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal otherPrice;      // Giá khác
    }

}
