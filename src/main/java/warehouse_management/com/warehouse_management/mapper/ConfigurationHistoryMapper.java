package warehouse_management.com.warehouse_management.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigVehicleSpecHistoryResponse;
import warehouse_management.com.warehouse_management.dto.configuration_history.response.ConfigurationHistoryResponse;
import warehouse_management.com.warehouse_management.model.ConfigurationHistory;
import warehouse_management.com.warehouse_management.model.InventoryItem;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConfigurationHistoryMapper {

    @Mapping(target = "id", ignore = true)
    ConfigurationHistory clone(ConfigurationHistory item);

    ConfigVehicleSpecHistoryResponse toConfigVehicleSpecHistoryResponse(InventoryItem item);

    ConfigurationHistoryResponse toConfigurationHistoryResponse(ConfigurationHistory item);
}
