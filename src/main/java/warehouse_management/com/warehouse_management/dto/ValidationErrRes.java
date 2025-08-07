package warehouse_management.com.warehouse_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationErrRes {
    private String field;
    private String message;
}