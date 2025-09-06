package warehouse_management.com.warehouse_management.dto.auth.response;

import lombok.Data;
import warehouse_management.com.warehouse_management.annotation.Validation;

import java.util.List;

@Data
public class LoginResponse {
    private String token;

    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private String avatar;


}