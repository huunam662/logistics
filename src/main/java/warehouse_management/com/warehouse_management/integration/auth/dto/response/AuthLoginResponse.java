package warehouse_management.com.warehouse_management.integration.auth.dto.response;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

@Data
public class AuthLoginResponse {
    private Boolean success;
    private String token;
}