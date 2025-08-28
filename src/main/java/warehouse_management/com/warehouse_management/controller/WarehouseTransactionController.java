package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.request.ApprovalTicketDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.request.CreateWarehouseTransactionDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.response.WarehouseTransactionPageDto;
import warehouse_management.com.warehouse_management.enumerate.WarehouseTranType;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;
import warehouse_management.com.warehouse_management.service.WarehouseTransactionService;
import warehouse_management.com.warehouse_management.service.report.ReportService;

import java.io.ByteArrayInputStream;
import java.util.Map;

@RestController
@Tag(name = "Warehouse Transaction", description = "FOR ADMIN / VP")
@RequestMapping("/v1/warehouse-transaction")
@RequiredArgsConstructor
public class WarehouseTransactionController {

    private final WarehouseTransactionService warehouseTransferTicketService;
    private final ReportService reportService;

//    @GetMapping("/{ticketId}/inventory-items")
//    @Operation(
//            summary = "GET Lấy các mặt hàng đang trong phiếu duyệt",
//            description = "GET Lấy các mặt hàng đang trong phiếu duyệt"
//    )
//    public ApiResponse<?> approvalTicket(
//            @PathVariable("ticketId") String ticketId
//    ){
//        List<InventoryItemPoNumberDto> items = warehouseTransferTicketService.getItemsInTicket(ticketId);
//        return ApiResponse.success(items);
//    }

    @GetMapping("/page")
    @Operation(
            summary = "GET Lấy dữ liệu phiên điều chuyển nội bộ (Phân trang).",
            description = "GET Lấy dữ liệu phiên điều chuyển nội bộ (Phân trang)."
    )
    public ApiResponse<?> getPageWarehouseTransaction(@ModelAttribute PageOptionsDto optionsDto){
        Page<WarehouseTransactionPageDto> pageWarehouseTransferTicketDto = warehouseTransferTicketService.getPageWarehouseTransferTicket(optionsDto);
        return ApiResponse.success(new PageInfoDto<>(pageWarehouseTransferTicketDto));
    }

    @GetMapping("/page/dest_to_dest_transfer")
    @Operation(
            summary = "GET Lấy giao dịch điều chuyển nội bộ (Phân trang).",
            description = "Dùng cho giao dịch DEST_TO_DEST_TRANSFER"
    )
    public ApiResponse<?> getPageLocalTransfer(@ModelAttribute PageOptionsDto optionsDto){
        Page<WarehouseTransactionPageDto> page =
                warehouseTransferTicketService.getPageWarehouseTransferTicket(optionsDto, WarehouseTranType.DEST_TO_DEST_TRANSFER);
        return ApiResponse.success(new PageInfoDto<>(page));
    }

    @GetMapping("/page/data_entry")
    @Operation(
            summary = "GET Lấy giao dịch nhập/xuất kho (Phân trang).",
            description = "Dùng cho giao dịch DATA_ENTRY"
    )
    public ApiResponse<?> getPageWarehouseInOut(@ModelAttribute PageOptionsDto optionsDto){
        Page<WarehouseTransactionPageDto> page =
                warehouseTransferTicketService.getPageWarehouseTransferTicket(optionsDto, WarehouseTranType.DATA_ENTRY);
        return ApiResponse.success(new PageInfoDto<>(page));
    }

    @GetMapping("/page/sale_right_transfer")
    @Operation(
            summary = "GET Lấy giao dịch điều chuyển quyền bán hàng (Phân trang).",
            description = "Dùng cho giao dịch SALE_RIGHT_TRANSFER"
    )
    public ApiResponse<?> getPageSaleRightTransfer(@ModelAttribute PageOptionsDto optionsDto){
        Page<WarehouseTransactionPageDto> page =
                warehouseTransferTicketService.getPageWarehouseTransferTicket(optionsDto, WarehouseTranType.SALE_RIGHT_TRANSFER);
        return ApiResponse.success(new PageInfoDto<>(page));
    }



    @PatchMapping("/{ticketId}/approval-status")
    @Operation(
            summary = "PATCH Duyệt / Hủy phiên điều chuyển nội bộ.",
            description = "PATCH Duyệt / Hủy phiên điều chuyển nội bộ."
    )
    public ResponseEntity<?> approvalTransaction(
            @PathVariable("ticketId") String ticketId,
            @Valid @RequestBody ApprovalTicketDto dto
    ){
        WarehouseTransaction ticket = warehouseTransferTicketService.approvalTransaction(ticketId, dto);
        ApiResponse<?> res = ApiResponse.success();
        res.setMessage("Cập nhật trạng thái phiên điều chuyển "+ticket.getTicketCode()+" thành công.");
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{ticketId}")
    @Operation(
            summary = "GET chi tiết phiên điều chuyển nội bộ.",
            description = "GET chi tiết phiên điều chuyển nội bộ."
    )
    public ApiResponse<?> getById(
            @PathVariable("ticketId") String ticketId
    ) {
        return ApiResponse.success(warehouseTransferTicketService.getById(ticketId));
    }

    @GetMapping("/export-report/{tranModule}/{tranId}")
    @Operation(
            summary = "GET xuất excel DCNB theo ticket id",
            description = "GET xuất excel DCNB theo ticket id"
    )
    public ResponseEntity<InputStreamResource> getExcelReportDCNB(
            @PathVariable("tranModule") String tranModule,
            @PathVariable("tranId") String tranId,
            @RequestParam(name = "docType", required = false) String docType
    ) {
        // type sẽ là "in" nếu không truyền, hoặc "out" nếu truyền ?type=out
        byte[] excelBytes = reportService.getReport(tranModule, tranId, docType);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(excelBytes));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report_generated.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(excelBytes.length)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    @PostMapping
    @Operation(
            summary = "POST Tạo phiên điều chuyển nội bộ.",
            description = "POST Tạo phiên điều chuyển nội bộ."
    )
    public ResponseEntity<?> createTicket(@Valid @RequestBody CreateWarehouseTransactionDto dto){
        WarehouseTransaction ticket = warehouseTransferTicketService.createWarehouseTransaction(dto);
        ApiResponse<?> res = ApiResponse.success(Map.of("ticketId", ticket.getId()));
        return ResponseEntity.ok(res);
    }

}
