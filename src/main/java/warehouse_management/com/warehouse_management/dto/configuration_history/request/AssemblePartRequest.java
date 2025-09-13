package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

@Data
public class AssemblePartRequest {
    private String vehicleId;
    private String componentType;
    private Integer componentQuantity;
    private String warehouseId;
}
