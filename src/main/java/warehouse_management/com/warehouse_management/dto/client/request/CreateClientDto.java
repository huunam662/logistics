package warehouse_management.com.warehouse_management.dto.client.request;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

@Data
public class CreateClientDto {
    @Validation(label = "Tên Khách hàng", required = true)
    private String name;
    private String address;

}
