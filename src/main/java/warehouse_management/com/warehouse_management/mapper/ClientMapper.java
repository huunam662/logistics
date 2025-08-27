package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import warehouse_management.com.warehouse_management.dto.client.request.CreateClientDto;
import warehouse_management.com.warehouse_management.dto.client.request.UpdateClientDto;
import warehouse_management.com.warehouse_management.model.Client;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {
    Client toClient(CreateClientDto dto);

    // update vào entity hiện tại
    void updateClientFromDto(UpdateClientDto dto, @MappingTarget Client entity);
}
