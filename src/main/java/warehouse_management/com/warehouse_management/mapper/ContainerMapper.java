package warehouse_management.com.warehouse_management.mapper;

import org.bson.types.ObjectId;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import warehouse_management.com.warehouse_management.dto.container.response.ContainerResponseDto;
import warehouse_management.com.warehouse_management.dto.warehouse.response.WarehouseResponseDto;
import warehouse_management.com.warehouse_management.model.Container;
import warehouse_management.com.warehouse_management.model.Warehouse;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ContainerMapper {
    @Mapping(source = "id", target = "id")
    ContainerResponseDto toResponseDto(Container entity);

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
