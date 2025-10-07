package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.integration.user.client.UserIntegrationClient;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserListIRes;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserIDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserIntegrationClient userIntegrationClient;
    private final CustomAuthentication customAuthentication;

    
    /**
     * Get users by role name từ .NET API
     */
    public List<UserIDto> getUsersByRole(String roleCode) {
        UserListIRes userListIRes = userIntegrationClient.getUsersByRole(customAuthentication.getUser().getAnatk(), roleCode);
        
        // Check success ở tầng service
        if (!userListIRes.getSuccess()) {
            throw LogicErrException.of("Lỗi lấy users theo role: API trả về success = false");
        }
        
        List<UserIDto> users = userListIRes.getData().getCollection();
        
        // Test loop - sẽ hoạt động vì UserIDto đã được deserialize đúng cách
        for(UserIDto u : users) {
            System.out.println("User: " + u.getEmail() + " - " + u.getRoleName());
        }
        
        return users;
    }
}
