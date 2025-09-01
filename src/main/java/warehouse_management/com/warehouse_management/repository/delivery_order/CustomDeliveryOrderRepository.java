package warehouse_management.com.warehouse_management.repository.delivery_order;

import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.delivery_order.response.DeliveryOrderPageDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;

public interface CustomDeliveryOrderRepository {

    Page<DeliveryOrderPageDto> findPageDeliveryOrder(PageOptionsDto optionsDto);
}
