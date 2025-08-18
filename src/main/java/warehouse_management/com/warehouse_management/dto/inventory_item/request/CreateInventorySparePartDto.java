package warehouse_management.com.warehouse_management.dto.inventory_item.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class CreateInventorySparePartDto {
    private String poNumber;
    private String commodityCode;
    private Integer quantity;
    private String description;
    private String orderDate;
    private String model;
    private String notes;                  // Ghi chú chung – Không bắt buộc
    private String contractNumber; // Số hợp đồng
    private Pricing pricing;
    private String warehouseId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pricing {
        private BigDecimal purchasePrice;       // Giá mua vào
        private BigDecimal salePriceR0;         // Giá bán đề xuất R0
        private BigDecimal salePriceR1;         // Giá bán đề xuất R1
        private BigDecimal actualSalePrice;
    }
}
