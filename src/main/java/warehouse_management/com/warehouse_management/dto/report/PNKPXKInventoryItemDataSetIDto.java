package warehouse_management.com.warehouse_management.dto.report;

import lombok.Data;

@Data
public class PNKPXKInventoryItemDataSetIDto {
    private int index;     // STT
    private String name;
    private String code;
    private String unit;
    private Integer quantity;
    private Integer realQuantity;


}
