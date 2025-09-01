package warehouse_management.com.warehouse_management.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;
import warehouse_management.com.warehouse_management.repository.inventory_item.InventoryItemRepository;

@Service
@RequiredArgsConstructor
public class ReportInventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    public Page<ReportInventoryDto> getPageReportInventoryToDashBoard(ReportParamsDto params){
        return inventoryItemRepository.findPageReportInventoryToDashBoard(params);
    }
}
