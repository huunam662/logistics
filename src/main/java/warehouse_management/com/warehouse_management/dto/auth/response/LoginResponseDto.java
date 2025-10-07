package warehouse_management.com.warehouse_management.dto.auth.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponseDto {
    private String token;
    private List<String> permissions;


}