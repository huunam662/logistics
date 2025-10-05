package warehouse_management.com.warehouse_management.integration.anabase;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.IntegrationException;
import warehouse_management.com.warehouse_management.integration.IntegrationUtils;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListResponse;

@Component
public class GenericIntegrationClient {

    private final IntegrationUtils integrationUtils;

    public GenericIntegrationClient(IntegrationUtils integrationUtils) {
        this.integrationUtils = integrationUtils;
    }

    /**
     * Generic method để call .NET API với SieveModel query parameters
     * 
     * @param interfaceCode Mã interface từ GeneralUtil (VD: GET_CUSTOMERS, GET_PRODUCTS)
     * @param token JWT token để authenticate
     * @param queryParams Query parameters theo SieveModel format (VD: "page=1&pageSize=10&filters=status==ACTIVE")
     * @param responseType Class type của response (VD: CustomerDto.class)
     * @param <T> Generic type cho response data
     * @return BaseListResponse<T> chứa data và pagination info
     */
    public <T> BaseListResponse<T> getList(String interfaceCode, String token, String queryParams) {
        try {
            String url = integrationUtils.getConnectUrl(interfaceCode);
            String fullUrl = queryParams != null && !queryParams.isEmpty() 
                ? url + "?" + queryParams 
                : url;
            
            BaseListResponse<T> response = integrationUtils.performGet(fullUrl, token, BaseListResponse.class);
            
            if (!response.getSuccess()) {
                throw IntegrationException.of("Lỗi tích hợp " + interfaceCode);
            }
            return response;
        } catch (Exception e) {
            throw IntegrationException.of("Lỗi tích hợp " + interfaceCode + ": " + e.getMessage());
        }
    }

    /**
     * Overloaded method không cần queryParams
     */
    public <T> BaseListResponse<T> getList(String interfaceCode, String token) {
        return getList(interfaceCode, token, null);
    }
}
