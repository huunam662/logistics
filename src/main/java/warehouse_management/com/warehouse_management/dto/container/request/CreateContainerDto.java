package warehouse_management.com.warehouse_management.dto.container.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateContainerDto {
    private String containerCode;

    private String fromWareHouseId;
    private String toWarehouseId;

    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;

    private String note;
}
