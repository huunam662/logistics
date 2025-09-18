package warehouse_management.com.warehouse_management.dto.inventory_item.request.excelImport;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import warehouse_management.com.warehouse_management.annotation.Validation;

import java.math.BigDecimal;

@Data
public class ExcelImportProductionSparePartDto {
    // Thông tin ràng buộc khóa
    private ObjectId warehouseId;

    private String inventoryType;

    @Validation(label = "PO", required = true)
    private String poNumber;
    @Validation(label = "Mã hàng hóa", required = true)
    private String commodityCode;
    @Validation(label = "Số lượng", required = true)
    private Integer quantity;
    private String description;
    private String model;
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String contractNumber; // Số hợp đồng

    private Pricing pricing;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal actualSalePrice;
    }

    @Valid
    private Logistics logistics;
    @Data
    @AllArgsConstructor
    public static class Logistics {
        @Validation(label = "Ngày đặt hàng", required = true)
        @Schema(example = "yyyy-MM-dd")
        private String orderDate; // Ngày đặt hàng 2
        @Validation(label = "Ngày dự kiến SX xong", required = true)
        @Schema(example = "yyyy-MM-dd")
        private String estimateCompletionDate; // Chỉ dùng trong báo cáo hàng chờ SX 27
    }

}
