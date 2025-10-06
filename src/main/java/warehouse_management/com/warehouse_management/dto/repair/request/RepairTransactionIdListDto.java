package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class RepairTransactionIdListDto {

    private List<String> repairTransactionIds;

}
