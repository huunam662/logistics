package warehouse_management.com.warehouse_management.integration.user.client;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.anabase.GenericIntegrationClient;
import warehouse_management.com.warehouse_management.integration.user.dto.response.UserListRes;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

@Component
public class UserIntegrationClient {

    private final GenericIntegrationClient genericIntegrationClient;

    public UserIntegrationClient(GenericIntegrationClient genericIntegrationClient) {
        this.genericIntegrationClient = genericIntegrationClient;
    }

    /**
     * Get users by role name từ .NET API
     */
    public UserListRes getUsersByRole(String token, String roleCode) {
        // Sử dụng method linh hoạt với Map path parameters
        java.util.Map<String, String> pathParams = new java.util.HashMap<>();
        pathParams.put("roleCode", roleCode);
        return genericIntegrationClient.getListWithPathParams(GeneralUtil.GET_USERS_BY_ROLE, token, pathParams, UserListRes.class);
    }
}