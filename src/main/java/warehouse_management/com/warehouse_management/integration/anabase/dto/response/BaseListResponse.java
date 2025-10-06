package warehouse_management.com.warehouse_management.integration.anabase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BaseListResponse<T> {
    
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("data")
    private BaseListDataDto<T> data;
}
