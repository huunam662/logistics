package warehouse_management.com.warehouse_management.integration.customer.client;

import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.integration.anabase.GenericIntegrationClient;
import warehouse_management.com.warehouse_management.integration.customer.dto.request.CreateConsignmentCustomerIReq;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerListIRes;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CreateConsignmentCustomerIRes;
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
    public CustomerListIRes getCustomers(String token, String queryParams) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS, token, queryParams, CustomerListIRes.class);
    }

    
    /**
     * Get tất cả customers không phân trang
     */
    public CustomerListIRes getAllCustomers(String token) {
        return genericIntegrationClient.getList(GeneralUtil.GET_CUSTOMERS_ALL, token, CustomerListIRes.class);
    }
    
    /**
     * Tạo customer mới
     */
    public CreateConsignmentCustomerIRes createConsigmentCustomer(String token, CreateConsignmentCustomerIReq request) {
        return genericIntegrationClient.post(
                GeneralUtil.CREATE_CONSIGNMENT_CUSTOMER,
                token,
                request,
                CreateConsignmentCustomerIRes.class
        );
    }
}