package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor

public class CreateInventoryItemDto {

    // ====== Thông tin cơ bản ======

    @Validation(label = "PO", required = true)
    private String poNumber; // Số của Đơn đặt hàng (Purchase Order) 1


    @Validation(label = "Mã sản phẩm", required = true)
    private String productCode; // ID duy nhất cho từng xe/phụ kiện 4

    @Validation(label = "Model", required = true)
    private String model; // Model kỹ thuật 5

    @Validation(label = "Loại", required = true)
    private String type; // Xe / Phụ kiện 6

    @Validation(label = "Chủng loại", required = true)
    private String category; // Ví dụ: Reach Truck, Pallet Truck...7

    @Validation(label = "Số Seri", required = true)
    private String serialNumber; // Số series nhà máy 8

    // ====== Thông số kỹ thuật ======
    private Specifications specifications;
    private String initialCondition; // Nguyên trạng 11

    @Data
    @AllArgsConstructor
    public static class Specifications {
        private Boolean hasSideShift; // Có side shift không 9
        private String otherDetails; // Chi tiết khác 10

        private Integer liftingCapacityKg; // Sức nâng (Kg) 12
        private String chassisType; // Loại khung nâng 13
        private Integer liftingHeightMm; // Độ cao nâng (mm) 14
        private String engineType; // Động cơ 15
        private String batteryInfo; // Bình điện 16
        private String batterySpecification; // Thông số bình điện 17
        private String chargerSpecification; // Thông số sạc 18
        //        private String forkSpecification; // Thông số càng 19
        private Integer valveCount; // Số van 20
        private String inventoryType; // Loại kho 21
    }

    // ====== Giá cả ======
    @Valid
    private Pricing pricing;

    @Data
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice; // Giá mua  22
        private BigDecimal salePriceR0; // Giá bán R0 23
        private BigDecimal salePriceR1; // Giá bán R1 24
        private BigDecimal actualSalePrice; // Giá bán thực tế 25

        @Validation(label = "Đại lý", required = true)
        private String agent; // Tên đại lý hoặc khách hàng đặt hàng 3

    }

    // ====== Thông tin khác ======
    private String notes; // Ghi chú  26

    @Valid
    private Logistics logistics;

    @Data
    @AllArgsConstructor
    public static class Logistics {
        @Validation(label = "Ngày đặt hàng", required = true)
        private LocalDateTime orderDate; // Ngày đặt hàng 2
        @Validation(label = "Ngày dự kiến SX xong", required = true)
        private LocalDateTime estimateCompletionDate; // Chỉ dùng trong báo cáo hàng chờ SX 27
    }


}
