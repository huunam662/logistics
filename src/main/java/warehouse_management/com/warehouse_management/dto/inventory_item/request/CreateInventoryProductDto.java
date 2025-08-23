package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import warehouse_management.com.warehouse_management.annotation.Validation;

import java.math.BigDecimal;

@Data
public class CreateInventoryProductDto {

    // ====== Thông tin cơ bản ======

    private String poNumber; // Số của Đơn đặt hàng (Purchase Order) 1
    private String inventoryType;
    private String productCode; // ID duy nhất cho từng xe/phụ kiện 4
    private String model; // Model kỹ thuật 5
    private String category; // Ví dụ: Reach Truck, Pallet Truck...7
    private String serialNumber; // Số series nhà máy 8
    private Boolean initialCondition; // Nguyên trạng 11
    private String notes; // Ghi chú  26
    private String warehouseId; // Mã kho
    private String warehouseType; // Loại kho
    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private Specifications specifications;
    private Pricing pricing;
    private Logistics logistics;

    @Data
    public static class Specifications {
        private Integer liftingCapacityKg; // Sức nâng (Kg) 12
        private String chassisType; // Loại khung nâng 13
        private Integer liftingHeightMm; // Độ cao nâng (mm) 14
        private String engineType; // Động cơ 15
        private String batteryInfo; // Bình điện 16
        private String forkDimensions;          // Thông số càng
        private String batterySpecification; // Thông số bình điện 17
        private String chargerSpecification; // Thông số sạc 18
        private Integer valveCount; // Số van 20
        private Boolean hasSideShift; // Có side shift không 9
        private String otherDetails; // Chi tiết khác 10
    }

    @Data
    public static class Pricing {
        private BigDecimal purchasePrice; // Giá mua  22
        private BigDecimal salePriceR0; // Giá bán R0 23
        private BigDecimal salePriceR1; // Giá bán R1 24
        private String agent; // Tên đại lý hoặc khách hàng đặt hàng 3
    }

    @Data
    public static class Logistics {
        @Schema(example = "yyyy-MM-dd")
        private String orderDate; // Ngày đặt hàng 2
        @Schema(example = "yyyy-MM-dd")
        private String estimateCompletionDate; // Chỉ dùng trong báo cáo hàng chờ SX 27
    }


}
