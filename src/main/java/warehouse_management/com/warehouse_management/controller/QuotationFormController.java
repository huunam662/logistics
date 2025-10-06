package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.quotation_form.request.*;
import warehouse_management.com.warehouse_management.dto.quotation_form.response.QuotationFormPageDto;
import warehouse_management.com.warehouse_management.model.QuotationForm;
import warehouse_management.com.warehouse_management.service.QuotationFormService;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "Quotation Form")
@RequestMapping("/v1/quotation-form")
public class QuotationFormController {

    private final QuotationFormService quotationFormService;

    @Operation(
            summary = "GET Đơn báo giá.",
            description = "GET Đơn báo giá."
    )
    @GetMapping("/{id}")
    public ApiResponse<?> getQuotationFormDtoToId(@PathVariable("id") String id){
        return ApiResponse.success(quotationFormService.getQuotationFormDtoToId(new ObjectId(id)));
    }

    @Operation(
            summary = "DELETE Đơn báo giá.",
            description = "DELETE Đơn báo giá."
    )
    @DeleteMapping
    public ApiResponse<?> bulkSoftDeleteQuotationForm(@Valid @RequestBody BulkDeleteQuotationDto dto){

        quotationFormService.bulkSoftDeleteQuotationForm(dto);
        return ApiResponse.success();
    }

    @Operation(
            summary = "PUT Cập nhật đơn báo giá.",
            description = "PUT Cập nhật đơn báo giá."
    )
    @PutMapping
    public ApiResponse<?> updateQuotationFormToId(@Valid @RequestBody UpdateQuotationFormDto dto){

        QuotationForm quotationForm = quotationFormService.updateQuotationForm(dto);

        return ApiResponse.success(Map.of("quotationFormId", quotationForm.getId()));
    }

    @Operation(
            summary = "GET Danh sách Sản phẩm trong kho cho báo giá. (Phân trang)",
            description = "GET Danh sách Sản phẩm trong kho cho báo giá. (Phân trang)"
    )
    @GetMapping("/products/warehouse")
    public ApiResponse<?> getPageQuotationProductWarehouse(
            @ModelAttribute PageOptionsDto optionsDto
    ){
        return ApiResponse.success(new PageInfoDto<>(quotationFormService.getPageQuotationProductWarehouse(optionsDto)));
    }

    @Operation(
            summary = "GET Danh sách Hàng hóa trong kho cho báo giá.",
            description = "GET Danh sách Hàng hóa trong kho cho báo giá."
    )
    @GetMapping("/spare-parts/warehouse")
    public ApiResponse<?> getPageQuotationSparePartWarehouse(
            @ModelAttribute PageOptionsDto optionsDto,
            @RequestParam("customerLevel") String customerLevel
    ){
        return ApiResponse.success(new PageInfoDto<>(quotationFormService.getPageQuotationSparePartWarehouse(optionsDto)));
    }

    @Operation(
            summary = "POST Tạo đơn báo giá.",
            description = "POST Tạo đơn báo giá."
    )
    @PostMapping
    public ApiResponse<?> createQuotationForm(@Valid @RequestBody CreateQuotationFormDto dto){

        QuotationForm quotationForm = quotationFormService.createQuotationForm(dto);

        return ApiResponse.success(Map.of("quotationFormId", quotationForm.getId()));
    }

    @Operation(
            summary = "POST Thêm sản phẩm vào đơn.",
            description = "POST Thêm sản phẩm vào đơn."
    )
    @PostMapping("/products")
    public ApiResponse<?> fillProductToQuotationForm(@Valid @RequestBody FillProductForQuotationDto dto){

        QuotationForm quotationForm = quotationFormService.fillProductToQuotationForm(dto);

        return ApiResponse.success(Map.of("quotationFormId", quotationForm.getId()));
    }

    @Operation(
            summary = "POST Thêm phụ tùng vào đơn.",
            description = "POST Thêm phụ tùng vào đơn."
    )
    @PostMapping("/spare-parts")
    public ApiResponse<?> fillSparePartToQuotationForm(@Valid @RequestBody FillSparePartForQuotationDto dto){

        QuotationForm quotationForm = quotationFormService.fillSparePartToQuotationForm(dto);

        return ApiResponse.success(Map.of("quotationFormId", quotationForm.getId()));
    }

    @Operation(
            summary = "GET Danh sách Sản phẩm trong đơn báo giá.",
            description = "GET Danh sách Sản phẩm trong đơn báo giá."
    )
    @GetMapping("/{id}/products")
    public ApiResponse<?> getQuotationProductToQuotationId(
           @PathVariable("id") String quotationFormId
    ){
        return ApiResponse.success(quotationFormService.getQuotationProductToQuotationId(new ObjectId(quotationFormId)));
    }

    @Operation(
            summary = "GET Danh sách Phụ tùng trong đơn báo giá.",
            description = "GET Danh sách Phụ tùng trong đơn báo giá."
    )
    @GetMapping("/{id}/spare-parts")
    public ApiResponse<?> getQuotationSparePartToQuotationId(
            @PathVariable("id") String quotationFormId
    ){
        return ApiResponse.success(quotationFormService.getQuotationSparePartToQuotationId(new ObjectId(quotationFormId)));
    }

    @Operation(
            summary = "GET Danh sách đơn báo giá. (Phân trang)",
            description = "GET Danh sách đơn báo giá. (Phân trang)"
    )
    @GetMapping("/page")
    public ApiResponse<?> getPageQuotationForm(@ModelAttribute PageOptionsDto optionsDto){
        return ApiResponse.success(new PageInfoDto<>(quotationFormService.getPageQuotationForm(optionsDto)));
    }

}
