package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.service.OfficeService;

@RestController
@Tag(name = "Office")
@RequestMapping("/v1/offices")
@RequiredArgsConstructor
public class OfficeController {

    private final OfficeService officeService;

//    @PostMapping("/create-from-warehouse")
//    @Operation(
//            summary = "POST create office from warehouse",
//            description = "Tạo office từ warehouse thông qua .NET API. " +
//                    "Sử dụng intercode CREATE_OFFICE_FROM_WAREHOUSE."
//    )
//    public ApiResponse<OfficeIDto> createOfficeFromWarehouse(
//            @RequestBody CreateOfficeFromWarehouseIReq request
//    ) {
//        // Tạo office từ warehouse
//        OfficeIDto response = officeService.createOfficeFromWarehouse(request);
//
//        return ApiResponse.success(response);
//    }
}
