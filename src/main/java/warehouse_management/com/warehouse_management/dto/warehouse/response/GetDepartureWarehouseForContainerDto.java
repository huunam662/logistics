package warehouse_management.com.warehouse_management.dto.warehouse.response;

import lombok.Data;

@Data
public class GetDepartureWarehouseForContainerDto {
    String warehouseId;
    String warehouseCode;
    String warehouseName;
}
