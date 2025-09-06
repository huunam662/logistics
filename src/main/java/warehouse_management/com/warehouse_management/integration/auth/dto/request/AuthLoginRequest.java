package warehouse_management.com.warehouse_management.integration.auth.dto.request;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;
@Data
public class AuthLoginRequest {
    private String username;
    private String password;
}
