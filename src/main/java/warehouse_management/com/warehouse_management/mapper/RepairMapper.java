package warehouse_management.com.warehouse_management.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import warehouse_management.com.warehouse_management.dto.repair.request.CreateRepairDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairHistoryDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairResponseDto;
import warehouse_management.com.warehouse_management.dto.repair.response.RepairVehicleSpecHistoryDto;
import warehouse_management.com.warehouse_management.model.InventoryItem;
import warehouse_management.com.warehouse_management.model.Repair;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface RepairMapper {

    Repair toRepair(CreateRepairDto dto);

    RepairVehicleSpecHistoryDto toRepairVehicleSpecHistoryDto(InventoryItem vehicle);

    RepairHistoryDto toRepairHistoryDto(Repair repair);

    default ObjectId map(String id){
        if(id == null) return null;
        return new ObjectId(id);
    }
}
