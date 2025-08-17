package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.request.CreateWarehouseTransferTicketDto;
import warehouse_management.com.warehouse_management.model.WarehouseTransferTicket;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WarehouseTransferTicketMapper {

    @Mapping(target = "originWarehouseId", ignore = true)
    @Mapping(target = "destinationWarehouseId", ignore = true)
    WarehouseTransferTicket toWarehouseTransferTicket(CreateWarehouseTransferTicketDto createWarehouseTransferTicketDto);

}
