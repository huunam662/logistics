package warehouse_management.com.warehouse_management.dto.report;

import lombok.Data;

@Data
public class InventoryItemDataSetIDto {
    private int index;     // STT
    private String name;
    private String serialNumber;
    private String unit;
    private Integer quantity;
    private String note;

}
