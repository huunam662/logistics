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
    public <T> T getList(String interfaceCode, String token, String queryParams, Class<T> responseType) {
        try {
            String url = integrationUtils.getConnectUrl(interfaceCode);
            String fullUrl = queryParams != null && !queryParams.isEmpty() 
                ? url + "?" + queryParams 
                : url;
            
            T response = integrationUtils.performGet(fullUrl, token, responseType);
            return response;
        } catch (Exception e) {
            throw IntegrationException.of("Lỗi tích hợp " + interfaceCode + ": " + e.getMessage());
        }
    }

    /**
     * Overloaded method không cần queryParams
     */
    public <T> T getList(String interfaceCode, String token, Class<T> responseType) {
        return getList(interfaceCode, token, null, responseType);
    }
    
    /**
     * Generic method để call .NET API với path parameters
     */
    public <T> T getListWithPathParams(String interfaceCode, String token, java.util.Map<String, String> pathParams, Class<T> responseType) {
        try {
            String url = integrationUtils.getConnectUrl(interfaceCode);
            String fullUrl = url;
            
            // Thay thế tất cả path parameters trong URL
            if (pathParams != null && !pathParams.isEmpty()) {
                for (java.util.Map.Entry<String, String> entry : pathParams.entrySet()) {
                    String placeholder = "{" + entry.getKey() + "}";
                    fullUrl = fullUrl.replace(placeholder, entry.getValue());
                }
            }
            
            T response = integrationUtils.performGet(fullUrl, token, responseType);
            
            return response;
        } catch (Exception e) {
            throw IntegrationException.of("Lỗi tích hợp " + interfaceCode + ": " + e.getMessage());
        }
    }
    
    /**
     * Generic method để call .NET API với path parameters + query parameters
     */
    public <T> T getListWithPathAndQueryParams(String interfaceCode, String token, 
            java.util.Map<String, String> pathParams, String queryParams, Class<T> responseType) {
        try {
            String url = integrationUtils.getConnectUrl(interfaceCode);
            String fullUrl = url;
            
            // Thay thế path parameters
            if (pathParams != null && !pathParams.isEmpty()) {
                for (java.util.Map.Entry<String, String> entry : pathParams.entrySet()) {
                    String placeholder = "{" + entry.getKey() + "}";
                    fullUrl = fullUrl.replace(placeholder, entry.getValue());
                }
            }
            
            // Thêm query parameters
            if (queryParams != null && !queryParams.isEmpty()) {
                fullUrl = fullUrl + "?" + queryParams;
            }
            
            T response = integrationUtils.performGet(fullUrl, token, responseType);
            
            return response;
        } catch (Exception e) {
            throw IntegrationException.of("Lỗi tích hợp " + interfaceCode + ": " + e.getMessage());
        }
    }
    
    /**
     * Generic method để call .NET API với POST request
     */
    public <T> T post(String interfaceCode, String token, Object requestBody, Class<T> responseType) {
        try {
            String url = integrationUtils.getConnectUrl(interfaceCode);
            T response = integrationUtils.performPost(url, token, requestBody, responseType);
            return response;
        } catch (Exception e) {
            throw IntegrationException.of("Lỗi tích hợp " + interfaceCode + ": " + e.getMessage());
        }
    }
}
