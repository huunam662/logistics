package warehouse_management.com.warehouse_management.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class ResultApiRes {

    private Boolean success;
    private String message;
    private String path;
    private Integer code;
    private Object data;
    @Setter(AccessLevel.NONE)
    private final Long timestamp = System.currentTimeMillis();

    public static ResultApiRes success(Object data, HttpServletRequest request){
        ResultApiRes res = new ResultApiRes();
        res.success = true;
        res.message = "Successful.";
        res.code = 200;
        res.data = data;
        res.path = request.getRequestURI();
        return res;
    }

}
