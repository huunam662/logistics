package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import warehouse_management.com.warehouse_management.dto.pagination.response.PageInfoDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;
import warehouse_management.com.warehouse_management.service.ReportInventoryItemService;

@RestController
@Tag(name = "Report inventory items")
@RequestMapping("/report-inventory-items")
@RequiredArgsConstructor
public class ReportInventoryItemController {

    private final ReportInventoryItemService reportInventoryItemService;

    @GetMapping
    public ResponseEntity<?> reportOnDashBoard(@ModelAttribute ReportParamsDto dto){
        Page<ReportInventoryDto> pageResult = reportInventoryItemService.getPageReportInventoryToDashBoard(dto);
        return ResponseEntity.ok().body(new PageInfoDto<>(pageResult));
    }

}
