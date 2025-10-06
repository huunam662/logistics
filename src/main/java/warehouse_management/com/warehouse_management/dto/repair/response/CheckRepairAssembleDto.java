package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDate;

@Data
public class CheckRepairAssembleDto {

    private ObjectId repairId;
    private String repairCode;
    private String status;
    private ObjectId componentId;
    private String serialNumber;
    private String componentName;
    private String warehouseCode;
    private String warehouseName;
    private LocalDate expectedCompletionDate;                   // Ngày dự kiến hoàn thành

}
