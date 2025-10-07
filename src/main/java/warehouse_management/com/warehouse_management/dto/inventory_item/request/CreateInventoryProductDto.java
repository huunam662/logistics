package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import warehouse_management.com.warehouse_management.annotation.Validation;

import java.math.BigDecimal;

@Data
public class CreateInventoryProductDto {

    // ====== Thông tin cơ bản ======

    @NotNull(message = "Mã PO là bắt buộc")
    @NotBlank(message = "Mã Po là bắt buộc")
    private String poNumber; // Số của Đơn đặt hàng (Purchase Order) 1

    @NotNull(message = "Loại sản phẩm là bắt buộc")
    @NotBlank(message = "Loại sản phẩm là bắt buộc")
    private String inventoryType;

    @NotNull(message = "Mã sản phẩm là bắt buộc")
    @NotBlank(message = "Mã sản phẩm là bắt buộc")
    private String productCode; // ID duy nhất cho từng xe/phụ kiện 4

    @NotNull(message = "Model là bắt buộc")
    @NotBlank(message = "Model là bắt buộc")
    private String model; // Model kỹ thuật 5

    @NotNull(message = "Chủng loại là bắt buộc")
    @NotBlank(message = "Chủng loại là bắt buộc")
    private String category; // Ví dụ: Reach Truck, Pallet Truck...7

    private String serialNumber; // Số series nhà máy 8
    private String notes; // Ghi chú  26

    private Integer manufacturingYear; // Năm sản xuất – Không bắt buộc
    private Boolean initialCondition;       // Mô tả nguyên trạng khi nhập kho – Không bắt buộc

    @NotNull(message = "Thông số kỹ thuật là bắt buộc")
    private Specifications specifications;

    @NotNull(message = "Giá sản phẩm là bắt buộc")
    private Pricing pricing;

    @NotNull(message = "Ngày đặt và dự kiến sản xuất là bắt buộc")
    private Logistics logistics;

    @Data
    public static class Specifications {
        private String liftingCapacityKg; // Sức nâng (Kg) 12
        private String chassisType; // Loại khung nâng 13
        private String liftingHeightMm; // Độ cao nâng (mm) 14
        private String engineType; // Động cơ 15
        private String batteryInfo; // Bình điện 16
        private String forkDimensions;          // Thông số càng
        private String batterySpecification; // Thông số bình điện 17
        private String chargerSpecification; // Thông số sạc 18
        private String valveCount; // Số van 20
        private String hasSideShift; // Có side shift không 9
        private String wheelInfo;    // Thông tin bánh xe
        private String otherDetails; // Chi tiết khác 10
    }

    @Data
    public static class Pricing {

        @NotNull(message = "Giá mua là bắt buộc")
        @Min(value = 0, message = "Giá mua phải lớn hơn hoặc bằng 0")
        private BigDecimal purchasePrice; // Giá mua  22

        @NotNull(message = "Giá bán R0 là bắt buộc")
        @Min(value = 0, message = "Giá bán R0 phải lớn hơn hoặc bằng 0")
        private BigDecimal salePriceR0; // Giá bán R0 23

        @NotNull(message = "Giá bán R1 là bắt buộc")
        @Min(value = 0, message = "Giá bán R1 phải lớn hơn hoặc bằng 0")
        private BigDecimal salePriceR1; // Giá bán R1 24

        @NotNull(message = "Giá khác là bắt buộc")
        @Min(value = 0, message = "Giá khác phải lớn hơn hoặc bằng 0")
        private BigDecimal otherPrice; // Giá khác

        @NotNull(message = "Đại lý là bắt buộc")
        @NotBlank(message = "Đại lý là bắt buộc")
        private String agent; // Tên đại lý hoặc khách hàng đặt hàng 3
    }

    @Data
    public static class Logistics {

        @NotNull(message = "Ngày đặt hàng là bắt buộc")
        @NotBlank(message = "Ngày đặt hàng là bắt buộc")
        @Schema(example = "yyyy-MM-dd")
        private String orderDate; // Ngày đặt hàng 2

        @NotNull(message = "Ngày đặt hàng là bắt buộc")
        @NotBlank(message = "Ngày đặt hàng là bắt buộc")
        @Schema(example = "yyyy-MM-dd")
        private String estimateCompletionDate; // Chỉ dùng trong báo cáo hàng chờ SX 27
    }

    //REF

    @NotNull(message = "Kho chứa hàng là bắt buộc")
    @NotBlank(message = "Kho chứa hàng là bắt buộc")
    private String warehouseId; // Mã kho

    @NotNull(message = "Loại kho của sản phẩm là bắt buộc")
    @NotBlank(message = "Loại kho của sản phẩm là bắt buộc")
    private String warehouseType; // Loại kho

}
