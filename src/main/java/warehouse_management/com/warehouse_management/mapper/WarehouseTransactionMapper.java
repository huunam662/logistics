package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import warehouse_management.com.warehouse_management.dto.warehouse_transaction.request.CreateWarehouseTransactionDto;
import warehouse_management.com.warehouse_management.model.WarehouseTransaction;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WarehouseTransactionMapper {

    @Mapping(target = "originWarehouseId", ignore = true)
    @Mapping(target = "destinationWarehouseId", ignore = true)
    WarehouseTransaction toWarehouseTransaction(CreateWarehouseTransactionDto createWarehouseTransactionDto);

}
