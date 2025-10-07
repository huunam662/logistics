package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import warehouse_management.com.warehouse_management.app.CustomAuthentication;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.customer.request.CreateConsignmentCustomerReq;
import warehouse_management.com.warehouse_management.enumerate.ActiveStatus;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;
import warehouse_management.com.warehouse_management.integration.customer.client.CustomerIntegrationClient;
import warehouse_management.com.warehouse_management.integration.customer.dto.request.CreateConsignmentCustomerIReq;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CreateConsignmentCustomerIDto;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerIDto;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerListIRes;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CreateConsignmentCustomerIRes;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;

import java.util.List;

import warehouse_management.com.warehouse_management.model.Warehouse;
import warehouse_management.com.warehouse_management.repository.warehouse.WarehouseRepository;
import warehouse_management.com.warehouse_management.utils.AnaConverterUtils;
import warehouse_management.com.warehouse_management.utils.GeneralUtil;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerIntegrationClient customerIntegrationClient;
    private final CustomAuthentication customAuthentication;
    private final OfficeService officeService;
    private final WarehouseRepository warehouseRepository;
    private final GeneralUtil generalUtil;

    /**
     * Get customers list từ .NET API
     * React sẽ build SieveModel query string và gửi lên
     * Spring chỉ forward query params đến .NET API
     *
     * @param token       JWT token để authenticate
     * @param queryParams Query parameters theo SieveModel format từ React
     * @return BaseListResponse<CustomerIDto> chứa danh sách customers và pagination info
     */
    public PageInfoDto<CustomerIDto> getCustomers(PageOptionsDto pageOptionsDto) {
        String queryString = AnaConverterUtils.convertToSieveQueryString(pageOptionsDto);
        CustomerListIRes customerListIRes = customerIntegrationClient.getCustomers(customAuthentication.getUser().getAnatk(), queryString);

        // Check success ở tầng service
        if (!customerListIRes.getSuccess()) {
            throw LogicErrException.of("Lỗi lấy danh sách customers: API trả về success = false");
        }

        List<CustomerIDto> customers = customerListIRes.getData().getCollection();

        // Test loop - sẽ hoạt động vì CustomerIDto đã được deserialize đúng cách
        for (CustomerIDto c : customers) {
            System.out.println("Customer (paginated): " + c.getCellPhone() + " - " + c.getEmail());
        }

        // Convert CustomerListIRes sang PageInfoDto
        PageInfoDto<CustomerIDto> pageInfo = AnaConverterUtils.convertBaseListResponseToPageInfo(customerListIRes, pageOptionsDto);

        return pageInfo;
    }


    /**
     * Get tất cả customers không phân trang
     */
    public List<CustomerIDto> getAllCustomers() {
        CustomerListIRes customerListIRes = customerIntegrationClient.getAllCustomers(customAuthentication.getUser().getAnatk());

        // Check success ở tầng service
        if (!customerListIRes.getSuccess()) {
            throw LogicErrException.of("Lỗi lấy tất cả customers: API trả về success = false");
        }

        List<CustomerIDto> customers = customerListIRes.getData().getCollection();

        // Test loop - sẽ hoạt động vì CustomerIDto đã được deserialize đúng cách
        for (CustomerIDto c : customers) {
            System.out.println("Customer: " + c.getCellPhone() + " - " + c.getEmail());
        }

        return customers;
    }

    /**
     * Tạo customer mới
     */
    @Transactional
    public CreateConsignmentCustomerIDto createConsigmentCustomer(CreateConsignmentCustomerReq request) {
        // Tạo các field chung
        String fullName = request.getFirstName() + " " + request.getLastName();
        String whName = "KHO KÝ GỬI - " + fullName;
        String whCode = generalUtil.generateWarehouseCode(request.getLastName(), request.getCellPhone());
        
        // Convert từ React DTO sang Integration DTO
        CreateConsignmentCustomerIReq integrationReq = CreateConsignmentCustomerIReq.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .cellPhone(request.getCellPhone())
                .password(request.getPassword())
                .email(request.getEmail())
                .address(request.getAddress())
                .customerLevelCode(request.getCustomerLevelCode())
                .officeName(whName)
                .officeCode(whCode)
                .officeTypeCode(WarehouseType.CONSIGNMENT.getOfficeTypeCode())
                .build();

        // Tạo warehouse trước
        Warehouse wh = new Warehouse();
        wh.setStatus(ActiveStatus.ACTIVE);
        wh.setCode(whName);
        wh.setName(whCode);

        // Gọi .NET API
        CreateConsignmentCustomerIRes response = customerIntegrationClient.createConsigmentCustomer(
                customAuthentication.getUser().getAnatk(),
                integrationReq
        );

        // Check success ở tầng service
        if (!response.getSuccess()) {
            throw LogicErrException.of("Lỗi tạo customer: API trả về success = false");
        }

        CreateConsignmentCustomerIDto dto = response.getData();
        wh.setOfficeId(dto.getOfficeId());
        warehouseRepository.save(wh);

        return dto;
    }


}

