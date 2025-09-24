package warehouse_management.com.warehouse_management.dto.configuration_history.request;

import lombok.Data;

@Data
public class UpdateStatusConfigurationDto {

    private String configurationId;
    private String status;

}
