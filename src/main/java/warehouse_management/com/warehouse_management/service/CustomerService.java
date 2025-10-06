package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.integration.customer.client.CustomerIntegrationClient;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerListRes;
import warehouse_management.com.warehouse_management.utils.AnaConverterUtils;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerIntegrationClient customerIntegrationClient;
    private final  CustomAuthentication customAuthentication;

    /**
     * Get customers list từ .NET API
     * React sẽ build SieveModel query string và gửi lên
     * Spring chỉ forward query params đến .NET API
     *
     * @param token       JWT token để authenticate
     * @param queryParams Query parameters theo SieveModel format từ React
     * @return BaseListResponse<CustomerDto> chứa danh sách customers và pagination info
     */
    public CustomerListRes getCustomers(PageOptionsDto pageOptionsDto) {
        String queryString = AnaConverterUtils.convertToSieveQueryString(pageOptionsDto);
        return customerIntegrationClient.getCustomers(customAuthentication.getUser().getAnatk(), queryString);
    }

    public CustomerListRes getCustomers() {
        return customerIntegrationClient.getCustomers(customAuthentication.getUser().getAnatk());
    }
    
    /**
     * Get tất cả customers không phân trang
     */
    public CustomerListRes getAllCustomers() {
        return customerIntegrationClient.getAllCustomers(customAuthentication.getUser().getAnatk());
    }


}

