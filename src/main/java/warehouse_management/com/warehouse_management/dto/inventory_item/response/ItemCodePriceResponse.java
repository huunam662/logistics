package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCodePriceResponse {

    private String code;
    private BigDecimal actualSalePrice;     // Giá bán thực tế
    private BigDecimal salePriceR0;         // Giá bán đề xuất R0
    private BigDecimal salePriceR1;         // Giá bán đề xuất R1

}
