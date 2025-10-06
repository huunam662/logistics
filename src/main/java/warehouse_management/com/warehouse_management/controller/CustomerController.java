package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.utils.AnaConverterUtils;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListResponse;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerDto;
import warehouse_management.com.warehouse_management.service.CustomerService;

@RestController
@Tag(name = "Customer")
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @Operation(
            summary = "GET danh sách customers",
            description = "Lấy danh sách customers từ .NET API. " +
                    "React gửi PageOptionsDto object, Spring build SieveModel query string và forward đến .NET API."
    )
    public ApiResponse<PageInfoDto<CustomerDto>> getCustomers(
            @ModelAttribute PageOptionsDto pageOptions
    ) {
        // Get data từ .NET API
        BaseListResponse<CustomerDto> baseListResponse = customerService.getCustomers(pageOptions);
        
        // Convert BaseListResponse sang PageInfoDto
        PageInfoDto<CustomerDto> pageInfo = AnaConverterUtils.convertBaseListResponseToPageInfo(baseListResponse, pageOptions);
        
        return ApiResponse.success(pageInfo);
    }
    
    @GetMapping("/all")
    @Operation(
            summary = "GET tất cả customers",
            description = "Lấy tất cả customers từ .NET API không phân trang. " +
                    "Sử dụng intercode GET_CUSTOMERS_ALL."
    )
    public ApiResponse<BaseListResponse<CustomerDto>> getAllCustomers() {
        // Get tất cả customers từ .NET API
        BaseListResponse<CustomerDto> baseListResponse = customerService.getAllCustomers();
        
        return ApiResponse.success(baseListResponse);
    }




}