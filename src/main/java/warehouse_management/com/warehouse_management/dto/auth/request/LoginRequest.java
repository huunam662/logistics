package warehouse_management.com.warehouse_management.dto.auth.request;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

@Data
public class LoginRequest {

    @Validation(label = "email", required = true)
    private String email;

    @Validation(label = "password", required = true)
    private String password;
}
