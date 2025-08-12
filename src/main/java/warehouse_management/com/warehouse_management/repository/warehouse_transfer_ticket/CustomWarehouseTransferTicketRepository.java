package warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket;

import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.WarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;

public interface CustomWarehouseTransferTicketRepository {

    Page<WarehouseTransferTicketDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto);

}
