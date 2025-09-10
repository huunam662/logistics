package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import warehouse_management.com.warehouse_management.dto.warranty.request.CreateWarrantyTransactionDTO;
import warehouse_management.com.warehouse_management.model.WarrantyTransaction;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface WarrantyTransactionMapper {
    WarrantyTransaction toWarrantyTransaction(CreateWarrantyTransactionDTO createWarrantyTransactionDTO);
}
