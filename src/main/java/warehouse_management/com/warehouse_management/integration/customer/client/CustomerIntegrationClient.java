package warehouse_management.com.warehouse_management.integration.customer.client;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.anabase.GenericIntegrationClient;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerListRes;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

@Component
public class CustomerIntegrationClient {

    private final GenericIntegrationClient genericIntegrationClient;

    public CustomerIntegrationClient(GenericIntegrationClient genericIntegrationClient) {
        this.genericIntegrationClient = genericIntegrationClient;
    }

    /**
     * Get customers list từ .NET API với SieveModel query parameters
     */
    public CustomerListRes getCustomers(String token, String queryParams) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS, token, queryParams, CustomerListRes.class);
    }

    
    /**
     * Get tất cả customers không phân trang
     */
    public CustomerListRes getAllCustomers(String token) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS_ALL, token, CustomerListRes.class);
    }
}