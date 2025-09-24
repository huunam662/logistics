package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

@Data
public class SendSwapComponentDto {

    private String leftVehicleId;
    private String rightVehicleId;
    private String componentType;


}
