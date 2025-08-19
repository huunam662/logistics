package warehouse_management.com.warehouse_management.repository.warehouse_transaction;

import org.springframework.data.domain.Page;
import warehouse_management.com.warehouse_management.dto.pagination.request.PageOptionsDto;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.response.WarehouseTransactionPageDto;

public interface CustomWarehouseTransactionRepository {

    Page<WarehouseTransactionPageDto> findPageWarehouseTransferTicket(PageOptionsDto optionsDto);

}
