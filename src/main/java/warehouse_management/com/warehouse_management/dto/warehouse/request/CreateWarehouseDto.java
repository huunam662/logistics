package warehouse_management.com.warehouse_management.dto.warehouse.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import warehouse_management.com.warehouse_management.anotation.Validation;

@Data
public class CreateWarehouseDto {

    @Validation(label = "name1", required = true)
    private String name;

    @Validation(label = "code", required = true)
    private String code;

    @Validation(required = true)
    private String type; // KHO CHO SX, KHO DI, KHO DEN

    private String status; // Ví dụ: "ACTIVE", "INACTIVE"

    private String address;

    private String managedById; // ID của người quản lý dưới dạng String

    private String note;
}
