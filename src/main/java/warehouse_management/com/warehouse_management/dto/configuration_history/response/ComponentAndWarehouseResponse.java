package warehouse_management.com.warehouse_management.dto.configuration_history.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ComponentAndWarehouseResponse {

    private ObjectId componentId;
    private String serialNumber;
    private String componentName;
    private String warehouseCode;
    private String warehouseName;
}
