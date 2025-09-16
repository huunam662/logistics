package warehouse_management.com.warehouse_management.dto.auth.response;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;

    private String fullName;
    private String email;
    private String phone;
    private String department;
    private String position;
    private String avatar;


}