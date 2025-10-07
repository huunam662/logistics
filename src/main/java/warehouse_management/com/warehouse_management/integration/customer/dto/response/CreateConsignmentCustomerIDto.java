package warehouse_management.com.warehouse_management.integration.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreateConsignmentCustomerIDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("is_customer")
    private Boolean isCustomer;

    @JsonProperty("cell_phone")
    private String cellPhone;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("employee_code")
    private String employeeCode;

    @JsonProperty("customer_level_id")
    private String customerLevelId;

    @JsonProperty("customer_level_name")
    private String customerLevelName;

    @JsonProperty("customer_level_note")
    private String customerLevelNote;

    //
    @JsonProperty("office_id")
    private String officeId;


}
