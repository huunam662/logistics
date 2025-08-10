package warehouse_management.com.warehouse_management.dto.container.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkDeleteContainerDto {
    private List<String> ids;
}
