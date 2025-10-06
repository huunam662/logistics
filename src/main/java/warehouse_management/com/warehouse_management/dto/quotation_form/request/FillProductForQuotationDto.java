package warehouse_management.com.warehouse_management.dto.quotation_form.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class FillProductForQuotationDto {

    private String quotationFormId;
    private List<QuotationProduct> quotationProducts = new ArrayList<>();
    private List<QuotationProductManual> quotationProductManuals = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationProduct{
        private String id;
        private Integer quantity;
        private BigDecimal salePrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuotationProductManual{
        private String poNumber;       // Số của Đơn đặt hàng (Purchase Order) – Bắt buộc
        private String productCode;    // Mã định danh của sản phẩm (đối với sản phẩm xe & phụ kiện, phụ tùng thuộc sản phẩm này) – Bắt buộc
        private String serialNumber;   // Số seri – Có cho xe/phụ kiện
        private String model;          // Model sản phẩm – Bắt buộc
        private String category;       // Chủng loại sản phẩm (VD: Ngồi lái) – Bắt buộc
        private String inventoryType;   // Loại hàng tồn (VD: phụ kiện, ...) - Bắt buộc
        private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
        private String warehouseType;  // Loại kho (kho bảo quản dành cho hàng hóa)
        private BigDecimal salePrice;         // Giá bán đề xuất theo cấp độ của khách hàng
        private String agent;
        private String notes;                  // Ghi chú chung – Không bắt buộc
        private Integer quantity;   // Số lượng hàng hóa
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
