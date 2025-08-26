package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import warehouse_management.com.warehouse_management.dto.client.request.CreateClientReq;
import warehouse_management.com.warehouse_management.dto.client.request.UpdateClientReq;
import warehouse_management.com.warehouse_management.dto.client.response.ClientRes;
import warehouse_management.com.warehouse_management.model.Client;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {
    Client toClient(CreateClientReq dto);

    // update vào entity hiện tại
    void updateClientFromDto(UpdateClientReq dto, @MappingTarget Client entity);
}
