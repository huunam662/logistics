package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ComponentAndWarehouseResponse {

    ObjectId componentId;
    String serialNumber;
    String componentName;
    String warehouseCode;
    String warehouseName;
}
