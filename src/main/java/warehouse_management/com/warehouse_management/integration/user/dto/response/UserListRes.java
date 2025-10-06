package warehouse_management.com.warehouse_management.integration.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListResponse;
import warehouse_management.com.warehouse_management.integration.anabase.dto.response.BaseListDataDto;

@Data
public class UserListRes extends BaseListResponse<UserDto> {

}
