package warehouse_management.com.warehouse_management.dto.warehouse.request;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

@Data
public class CreateWarehouseDto {

    @Validation(label = "name", required = true)
    private String name;

    @Validation(label = "code", required = true)
    private String code;

    @Validation(label = "type", required = true)
    private String type; // KHO CHO SX, KHO DI, KHO DEN

    private String status; // Ví dụ: "ACTIVE", "INACTIVE"

    private String address;

    private String managedById; // ID của người quản lý dưới dạng String

    private String note;
}
