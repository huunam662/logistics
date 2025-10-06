package warehouse_management.com.warehouse_management.integration.customer.client;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.anabase.GenericIntegrationClient;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerDto;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListResponse;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

@Component
public class CustomerIntegrationClient {

    private final GenericIntegrationClient genericIntegrationClient;

    public CustomerIntegrationClient(GenericIntegrationClient genericIntegrationClient) {
        this.genericIntegrationClient = genericIntegrationClient;
    }

    /**
     * Get customers list từ .NET API với SieveModel query parameters
     * 
     * @param token JWT token để authenticate
     * @param queryParams Query parameters theo SieveModel format (VD: "page=1&pageSize=10&filters=status==ACTIVE")
     * @return BaseListResponse<CustomerDto> chứa danh sách customers và pagination info
     */
    public BaseListResponse<CustomerDto> getCustomers(String token, String queryParams) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS, token, queryParams);
    }

    /**
     * Get customers list không có filter
     */
    public BaseListResponse<CustomerDto> getCustomers(String token) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS, token);
    }
    
    /**
     * Get tất cả customers không phân trang
     */
    public BaseListResponse<CustomerDto> getAllCustomers(String token) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS_ALL, token);
    }
}
