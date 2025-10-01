package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class CreateInventorySparePartDto {

    @NotNull(message = "Mã PO là bắt buộc")
    @NotBlank(message = "Mã Po là bắt buộc")
    private String poNumber;

    @NotNull(message = "Mã hàng hóa là bắt buộc")
    @NotBlank(message = "Mã hàng hóa là bắt buộc")
    private String commodityCode;

    @NotNull(message = "Số lượng là bắt buộc")
    @Min(value = 1, message = "Số lượng phải lớn hơn hoặc bằng 1")
    private Integer quantity;

    @NotNull(message = "Mô tả hàng hóa là bắt buộc")
    @NotBlank(message = "Mô tả hàng hóa là bắt buộc")
    private String description;

    @NotNull(message = "Ngày đặt hàng là bắt buộc")
    @NotBlank(message = "Ngày đặt hàng là bắt buộc")
    private String orderDate;

    @NotNull(message = "Model là bắt buộc")
    @NotBlank(message = "Model là bắt buộc")
    private String model;

    private String notes;                  // Ghi chú chung – Không bắt buộc

    private String contractNumber; // Số hợp đồng

    @NotNull(message = "Giá sản phẩm là bắt buộc")
    private Pricing pricing;

    @NotNull(message = "Kho chứa hàng là bắt buộc")
    @NotBlank(message = "Kho chứa hàng là bắt buộc")
    private String warehouseId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {

        @NotNull(message = "Giá mua là bắt buộc")
        @Min(value = 0, message = "Giá mua phải lớn hơn hoặc bằng 0")
        private BigDecimal purchasePrice;       // Giá mua vào

        @NotNull(message = "Giá bán R0 là bắt buộc")
        @Min(value = 0, message = "Giá bán R0 phải lớn hơn hoặc bằng 0")
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0

        @NotNull(message = "Giá bán R0 là bắt buộc")
        @Min(value = 0, message = "Giá bán R1 phải lớn hơn hoặc bằng 0")
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1

        @NotNull(message = "Giá khác là bắt buộc")
        @Min(value = 0, message = "Giá khác phải lớn hơn hoặc bằng 0")
        private BigDecimal otherPrice; // Giá khác
    }
}
