package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;
import java.util.List;

@Data
public class AddVehicleToConfigurationRequest {

    private List<String> vehicleIds;
}
