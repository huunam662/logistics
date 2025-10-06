package warehouse_management.com.warehouse_management.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("full_name")
    private String fullName;
    
    private String email;
    
    @JsonProperty("employee_code")
    private String employeeCode;
    
    private String department;
    
    @JsonProperty("cell_phone")
    private String cellPhone;
    
    private boolean disabled;
    
    @JsonProperty("role_name")
    private String roleName;
    
    @JsonProperty("role_id")
    private String roleId;
    
    @JsonProperty("office_id")
    private String officeId;
    
    @JsonProperty("office_name")
    private String officeName;
}