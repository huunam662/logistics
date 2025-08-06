package warehouse_management.com.warehouse_management.mapper.warehouse;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import warehouse_management.com.warehouse_management.dto.warehouse.request.CreateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.request.UpdateWarehouseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.model.Warehouse;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WarehouseMapper {

    @Mapping(source = "managedById", target = "managedBy")
    Warehouse toEntity(CreateWarehouseDto dto);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "managedBy", target = "managedById")
    WarehouseResponseDto toResponseDto(Warehouse entity);

    @Mapping(source = "managedById", target = "managedBy")
    void updateFromDto(UpdateWarehouseDto dto, @MappingTarget Warehouse entity);

    default ObjectId map(String value) {
        if (value == null) {
            return null;
        }
        return new ObjectId(value);
    }

    default String map(ObjectId value) {
        return value != null ? value.toHexString() : null;
    }
}
