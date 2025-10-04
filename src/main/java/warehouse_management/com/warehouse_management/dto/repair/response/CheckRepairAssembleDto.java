package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CheckRepairAssembleDto {

    private String repairCode;
    private String status;
    private ObjectId componentId;
    private String serialNumber;
    private String componentName;
    private String warehouseCode;
    private String warehouseName;

}
