package warehouse_management.com.warehouse_management.dto.warehouse.request;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

@Data
public class UpdateWarehouseDto {
    @Validation(label = "name", required = true)
    private String name;
    private String status;
    private String address;
    private String area;
    private String managedById;
    private String note;
}