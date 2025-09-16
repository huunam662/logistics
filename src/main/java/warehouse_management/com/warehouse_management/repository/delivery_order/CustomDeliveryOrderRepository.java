package warehouse_management.com.warehouse_management.repository.delivery_order;

import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.request.ReportParamsDto;
import warehouse_management.com.warehouse_management.dto.report_inventory.response.ReportInventoryDto;

public interface CustomDeliveryOrderRepository {

    Page<DeliveryOrderPageDto> findPageDeliveryOrder(PageOptionsDto optionsDto);

    Page<ReportInventoryDto> findPageReportItemUnDelivered(ReportParamsDto params);

}
