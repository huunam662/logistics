package warehouse_management.com.warehouse_management.dto.warehouse.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateWarehouseDto {

    @NotBlank(message = "Warehouse name is mandatory")
    private String name;

    @NotBlank(message = "Warehouse code is mandatory")
    private String code;

    @NotBlank(message = "Warehouse type is mandatory")
    private String type; // KHO CHO SX, KHO DI, KHO DEN,

    private String status; // Ví dụ: "ACTIVE", "INACTIVE"

    private String address;
    private String area;
    private String managedById; // ID của người quản lý dưới dạng String
    private String note;
}
