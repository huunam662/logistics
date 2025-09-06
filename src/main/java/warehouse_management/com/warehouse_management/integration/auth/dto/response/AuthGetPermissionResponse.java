package warehouse_management.com.warehouse_management.integration.auth.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class AuthGetPermissionResponse {
    private Boolean success;
    private List<String> data;
}
