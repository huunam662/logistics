package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;
import java.util.List;

@Data
public class AddVehicleToConfigurationDto {

    private List<String> vehicleIds;
}
