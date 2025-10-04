package warehouse_management.com.warehouse_management.dto.repair.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class CheckRepairDisassembleDto {

    private ObjectId repairId;
    private String repairCode;
    private String status;

}
