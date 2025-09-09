package warehouse_management.com.warehouse_management.dto.warehouse.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class IdAndNameWarehouseDto {

    private ObjectId id;
    private String name;

}
