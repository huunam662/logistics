package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairTransactionDto;
import warehouse_management.com.warehouse_management.model.RepairTransaction;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RepairTransactionMapper {

    RepairTransactionDto toRepairTransactionDto(RepairTransaction repairTransaction);

}
