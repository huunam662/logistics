package warehouse_management.com.warehouse_management.integration.auth.client;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.integration.IntegrationException;
import warehouse_management.com.warehouse_management.integration.IntegrationUtils;
import warehouse_management.com.warehouse_management.integration.auth.dto.request.AuthLoginRequest;
import warehouse_management.com.warehouse_management.integration.auth.dto.response.AuthGetInfoResponse;
import warehouse_management.com.warehouse_management.integration.auth.dto.response.AuthGetPermissionResponse;
import warehouse_management.com.warehouse_management.integration.auth.dto.response.AuthLoginResponse;
import warehouse_management.com.warehouse_management.integration.auth.exceptions.AuthIntegrationException;
import warehouse_management.com.warehouse_management.utils.GeneralResource;


import java.util.Map;

@Component
public class AuthIntegrationClient {

    private final IntegrationUtils integrationUtils;

    public AuthIntegrationClient(IntegrationUtils integrationUtils) {
        this.integrationUtils = integrationUtils;
    }

    public AuthLoginResponse login(AuthLoginRequest loginRequest) {
        try {
            Map<String, String> paramsMap = integrationUtils.toParamMap(loginRequest);
            String url = integrationUtils.getConnectUrl(GeneralResource.AUTH_LOGIN);
            AuthLoginResponse rs = integrationUtils.performPost(integrationUtils.buildUrlWithParams(url, paramsMap), integrationUtils.buildDefaultHeader(), AuthLoginResponse.class);
            if (!rs.getSuccess()) throw AuthIntegrationException.of("Lỗi tích hợp AUTH_LOGIN ");
            return rs;
        } catch (Exception e) {
            throw AuthIntegrationException.of("Lỗi tích hợp AUTH_LOGIN " + e.getMessage());
        }
    }

    public AuthGetInfoResponse getInfo(String token) {
        try {
            String url = integrationUtils.getConnectUrl(GeneralResource.AUTH_GET_INFO);
            AuthGetInfoResponse rs = integrationUtils.performGet(url, token, AuthGetInfoResponse.class);
            if (!rs.getSuccess()) throw AuthIntegrationException.of("Lỗi tích hợp AUTH_GET_INFO ");
            return rs;
        } catch (Exception e) {
            throw AuthIntegrationException.of("Lỗi tích hợp AUTH_GET_INFO " + e.getMessage());
        }
    }

    public AuthGetPermissionResponse getPermission(String token) {
        try {
            String url = integrationUtils.getConnectUrl(GeneralResource.AUTH_GET_PERMISSION);
            AuthGetPermissionResponse rs = integrationUtils.performGet(url, token, AuthGetPermissionResponse.class);
            if (!rs.getSuccess()) throw AuthIntegrationException.of("Lỗi tích hợp AUTH_GET_PERMISSION ");
            return rs;
        } catch (Exception e) {
            throw AuthIntegrationException.of("Lỗi tích hợp AUTH_GET_PERMISSION " + e.getMessage());
        }
    }


}
