package warehouse_management.com.warehouse_management.dto.inventory_item.response;

import lombok.Data;

@Data
public class ItemCodeModelSerialResponse {

    private String vehicleId;
    private String productCode;
    private String model;
    private String serialNumber;

}
