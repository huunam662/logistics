package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.integration.user.client.UserIntegrationClient;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserListRes;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserIntegrationClient userIntegrationClient;
    private final CustomAuthentication customAuthentication;

    
    /**
     * Get users by role name từ .NET API
     */
    public UserListRes getUsersByRole(String roleName) {
        return userIntegrationClient.getUsersByRole(customAuthentication.getUser().getAnatk(), roleName);
    }
}
