package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class ItemCodeModelSerialDto {

    private ObjectId vehicleId;
    private String productCode;
    private String model;
    private String serialNumber;

}
