package warehouse_management.com.warehouse_management.repository.warehouse_transfer_ticket;

import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.response.WarehouseTransferTicketPageDto;

public interface CustomWarehouseTransferTicketRepository {

    Page<WarehouseTransferTicketPageDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto);

}
