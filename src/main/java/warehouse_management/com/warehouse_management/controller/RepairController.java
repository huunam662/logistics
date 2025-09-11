package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.repair.request.CreateRepairDTO;
import warehouse_management.com.warehouse_management.dto.repair.request.CreateRepairTransactionDTO;
import warehouse_management.com.warehouse_management.dto.repair.request.UpdateStatusRepairDTO;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDTO;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairTransactionResponseDTO;
import warehouse_management.com.warehouse_management.service.RepairService;

import java.util.List;

@RestController
@Tag(name = "Repair")
@RequestMapping("/v1/repair")
@RequiredArgsConstructor
public class RepairController {
    private final RepairService repairService;

    @GetMapping
    @Operation(
            summary = "GET danh sách đơn sửa chữa",
            description = "GET danh sách đơn sửa chữa"
    )
    public ApiResponse<Page<RepairResponseDTO>> getListRepair(@ModelAttribute PageOptionsDto pageOptionsDto) {
        return ApiResponse.success(repairService.getListRepair(pageOptionsDto));
    }

    @PostMapping
    @Operation(
            summary = "Tạo đơn sửa chữa",
            description = "Tạo đơn sửa chữa"
    )
    public ApiResponse<List<RepairResponseDTO>> saveRepair(@Valid @RequestBody List<CreateRepairDTO> listCreateRepairDTO) {
        return ApiResponse.success(repairService.createRepair(listCreateRepairDTO));
    }

    @PostMapping("/transaction")
    @Operation(
            summary = "Tạo phiếu sửa chữa",
            description = "Tạo phiếu sửa chữa"
    )
    public ApiResponse<RepairTransactionResponseDTO> saveRepairTransaction(@Valid @RequestBody CreateRepairTransactionDTO createRepairTransactionDTO) {
        return ApiResponse.success(repairService.createRepairTransaction(createRepairTransactionDTO));
    }

    @PatchMapping("/status")
    @Operation(
            summary = "Cập nhật trạng thái đơn sửa chữa",
            description = "Cập nhật trạng thái đơn sửa chữa, có 3 trạng thái [IN_WARRANTY, COMPLETE, EXPIRED]"
    )
    public ApiResponse<RepairResponseDTO> updateStatusRepair(@Valid @RequestBody UpdateStatusRepairDTO updateStatusRepairDTO) {
        return ApiResponse.success(repairService.updateStatus(updateStatusRepairDTO));
    }
}
