package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CheckConfigurationSwapDto {

    private String configurationCode;
    private String status;
    private ObjectId vehicleId;
    private String productCode;
    private String model;
    private String serialNumber;

}
