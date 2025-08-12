package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.WarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.dto.inventory_item.response.InventoryItemPoNumberDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.service.WarehouseTransferTicketService;

import java.util.List;

@RestController
@Tag(name = "Warehouse Transfer Ticket", description = "FOR ADMIN / VP")
@RequestMapping("/v1/warehouse-transfer-ticket")
@RequiredArgsConstructor
public class WarehouseTransferTicketController {

    private final WarehouseTransferTicketService warehouseTransferTicketService;

    @GetMapping("/{ticketId}/inventory-items")
    @Operation(
            summary = "GET Lấy các mặt hàng đang trong phiếu duyệt",
            description = "GET Lấy các mặt hàng đang trong phiếu duyệt"
    )
    public ApiResponse<?> approvalTicket(
            @PathVariable("ticketId") String ticketId
    ){
        List<InventoryItemPoNumberDto> items = warehouseTransferTicketService.getItemsInTicket(ticketId);
        return ApiResponse.success(items);
    }

    @GetMapping("/page")
    @Operation(
            summary = "GET Lấy dữ liệu Phiếu chuyển duyệt (Phân trang).",
            description = "GET Lấy dữ liệu Phiếu chuyển duyệt (Phân trang)."
    )
    public ApiResponse<?> getPageWarehouseTransferTicket(@ModelAttribute PageOptionsDto optionsDto){
        Page<WarehouseTransferTicketDto> pageWarehouseTransferTicketDto = warehouseTransferTicketService.getPageWarehouseTransferTicket(optionsDto);
        return ApiResponse.success(new PageInfoDto<>(pageWarehouseTransferTicketDto));
    }

    @PatchMapping("/{ticketId}/approval-status")
    @Operation(
            summary = "PATCH Duyệt / Hủy phiếu.",
            description = "PATCH Duyệt / Hủy phiếu."
    )
    public void approvalTransferTicket(
            @PathVariable("ticketId") String ticketId,
            @Parameter(description = "[APPROVED, REJECTED]")
            @RequestParam("status") String status
    ){
        warehouseTransferTicketService.approvalTransferTicket(ticketId, status);
    }

}
