package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warranty.request.CreateWarrantyDTO;
import warehouse_management.com.warehouse_management.dto.warranty.request.UpdateStatusWarrantyRequestDTO;
import warehouse_management.com.warehouse_management.dto.warranty.response.WarrantyResponseDTO;
import warehouse_management.com.warehouse_management.service.WarrantyService;

import java.util.List;

@RestController
@Tag(name = "Warranty")
@RequestMapping("/v1/warranty")
@RequiredArgsConstructor
public class WarrantyController {
    @Autowired
    private WarrantyService warrantyService;

    @GetMapping
    @Operation(
            summary = "GET danh sách đơn bảo hành",
            description = "GET danh sách đơn bảo hành"
    )
    public ApiResponse<?> getListWarranty(@ModelAttribute PageOptionsDto pageOptionsDto) {
        return ApiResponse.success(warrantyService.getListWarranty(pageOptionsDto));
    }

    @PostMapping
    @Operation(
            summary = "Tạo đơn bảo hành",
            description = "Tạo đơn bảo hành"
    )
    public ApiResponse<List<WarrantyResponseDTO>> saveWarranty(@Valid @RequestBody List<CreateWarrantyDTO> listCreateWarrantyDTO) {
        return ApiResponse.success(warrantyService.createWarranty(listCreateWarrantyDTO));
    }

    @PatchMapping("/status")
    @Operation(
            summary = "Cập nhật trạng thái đơn bảo hành",
            description = "Cập nhật trạng thái đơn bảo hành, có 3 trạng thái [IN_WARRANTY, COMPLETE, EXPIRED]"
    )
    public ApiResponse<WarrantyResponseDTO> updateStatusWarranty(@Valid @RequestBody UpdateStatusWarrantyRequestDTO updateStatusWarrantyRequestDTO) {
        return ApiResponse.success(warrantyService.updateStatus(updateStatusWarrantyRequestDTO));
    }
}
