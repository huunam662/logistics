package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.integration.customer.client.CustomerIntegrationClient;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerListRes;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.util.List;
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
    public PageInfoDto<CustomerDto> getCustomers(PageOptionsDto pageOptionsDto) {
        String queryString = AnaConverterUtils.convertToSieveQueryString(pageOptionsDto);
        CustomerListRes customerListRes = customerIntegrationClient.getCustomers(customAuthentication.getUser().getAnatk(), queryString);
        
        // Check success ở tầng service
        if (!customerListRes.getSuccess()) {
            throw LogicErrException.of("Lỗi lấy danh sách customers: API trả về success = false");
        }
        
        List<CustomerDto> customers = customerListRes.getData().getCollection();
        
        // Test loop - sẽ hoạt động vì CustomerDto đã được deserialize đúng cách
        for(CustomerDto c : customers) {
            System.out.println("Customer (paginated): " + c.getCellPhone() + " - " + c.getEmail());
        }
        
        // Convert CustomerListRes sang PageInfoDto
        PageInfoDto<CustomerDto> pageInfo = AnaConverterUtils.convertBaseListResponseToPageInfo(customerListRes, pageOptionsDto);
        
        return pageInfo;
    }

    
    /**
     * Get tất cả customers không phân trang
     */
    public List<CustomerDto> getAllCustomers() {
        CustomerListRes customerListRes = customerIntegrationClient.getAllCustomers(customAuthentication.getUser().getAnatk());
        
        // Check success ở tầng service
        if (!customerListRes.getSuccess()) {
            throw LogicErrException.of("Lỗi lấy tất cả customers: API trả về success = false");
        }
        
        List<CustomerDto> customers = customerListRes.getData().getCollection();
        
        // Test loop - sẽ hoạt động vì CustomerDto đã được deserialize đúng cách
        for(CustomerDto c : customers) {
            System.out.println("Customer: " + c.getCellPhone() + " - " + c.getEmail());
        }
        
        return customers;
    }


}

