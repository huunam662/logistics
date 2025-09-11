package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDTO;
import warehouse_management.com.warehouse_management.model.Repair;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RepairMapper {

    @Mapping(source = "repairInventoryItem.productCode", target = "repairInventoryItemProductCode")
    @Mapping(source = "repairInventoryItem.model", target = "repairInventoryItemModel")
    @Mapping(source = "repairInventoryItem.serialNumber", target = "repairInventoryItemSerialNumber")
    RepairResponseDTO toResponseDto(Repair entity);
}
