package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.customer.request.CreateConsignmentCustomerReq;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CreateConsignmentCustomerIDto;
import warehouse_management.com.warehouse_management.integration.customer.dto.response.CustomerIDto;
import warehouse_management.com.warehouse_management.service.CustomerService;

import java.util.List;

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
    public ApiResponse<PageInfoDto<CustomerIDto>> getCustomers(
            @ModelAttribute PageOptionsDto pageOptions
    ) {
        // Get data từ Service (đã có for loop test và convert sang PageInfoDto bên trong)
        PageInfoDto<CustomerIDto> pageInfo = customerService.getCustomers(pageOptions);
        
        return ApiResponse.success(pageInfo);
    }
    
    @GetMapping("/all")
    @Operation(
            summary = "GET tất cả customers",
            description = "Lấy tất cả customers từ .NET API không phân trang. " +
                    "Sử dụng intercode GET_CUSTOMERS_ALL."
    )
    public ApiResponse<List<CustomerIDto>> getAllCustomers() {
        // Get tất cả customers từ Service (đã có for loop test bên trong)
        List<CustomerIDto> customers = customerService.getAllCustomers();
        
        return ApiResponse.success(customers);
    }
    
    @PostMapping("/createConsignmentCustomer")
    @Operation(
            summary = "POST tạo customer mới",
            description = "Tạo customer mới thông qua .NET API. " +
                    "Sử dụng intercode CREATE_CONSIGNMENT_CUSTOMER."
    )
    public ApiResponse<?> createConsigmentCustomer(
            @RequestBody CreateConsignmentCustomerReq request
    ) {
        // Tạo customer mới
        CreateConsignmentCustomerIDto response = customerService.createConsigmentCustomer(request);
        
        return ApiResponse.success(response);
    }

}