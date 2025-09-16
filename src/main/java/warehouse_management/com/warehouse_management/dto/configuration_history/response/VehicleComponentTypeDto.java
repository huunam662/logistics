package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class VehicleComponentTypeDto {

    private ObjectId componentId;

    private String componentType;

    private String componentName;

}
