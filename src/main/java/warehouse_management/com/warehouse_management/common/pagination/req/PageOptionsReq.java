package warehouse_management.com.warehouse_management.common.pagination.req;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.List;

@Data
@NoArgsConstructor
@ParameterObject
public class PageOptionsReq {

    @Parameter(example = "1")
    private Integer page = 1;
    @Parameter(example = "10")
    private Integer size = 10;
    private List<String> sortBy;
    private Sort.Direction direction;
    private String filter;

    public Pageable getPageable(){
        if(sortBy == null || sortBy.isEmpty() || direction == null)
            return PageRequest.of(page - 1, size);
        return PageRequest.of(page - 1, size, Sort.by(direction, sortBy.toArray(String[]::new)));
    }

}
