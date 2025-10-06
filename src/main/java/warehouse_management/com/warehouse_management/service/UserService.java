package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.integration.user.client.UserIntegrationClient;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserListRes;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserIntegrationClient userIntegrationClient;
    private final CustomAuthentication customAuthentication;

    
    /**
     * Get users by role name từ .NET API
     */
    public List<UserDto> getUsersByRole(String roleName) {
        UserListRes userListRes = userIntegrationClient.getUsersByRole(customAuthentication.getUser().getAnatk(), roleName);
        List<UserDto> users = userListRes.getData().getCollection();
        
        // Test loop - sẽ hoạt động vì UserDto đã được deserialize đúng cách
        for(UserDto u : users) {
            System.out.println("User: " + u.getEmail() + " - " + u.getRoleName());
        }
        
        return users;
    }
}
