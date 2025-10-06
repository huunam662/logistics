package warehouse_management.com.warehouse_management.integration.anabase.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class BaseListDataDto<T> {
    
    @JsonProperty("collection")
    private List<T> collection;
    
    @JsonProperty("total")
    private Integer total;
    
    @JsonProperty("pagesize")
    private Integer pageSize;
    
    @JsonProperty("pageIndex")
    private Integer pageIndex;
}
