package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SendConfigAssembleDto {

    private String vehicleId;                 // Sản phẩm được bảo hành
    private String componentId;

}
