package warehouse_management.com.warehouse_management.dto.warehouse.request;

import lombok.Data;

@Data
public class UpdateWarehouseDto {
    private String name;
    private String status;
    private String address;
    private String area;
    private String managedById;
    private String note;
}