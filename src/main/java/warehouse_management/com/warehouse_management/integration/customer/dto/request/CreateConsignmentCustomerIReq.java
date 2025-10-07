package warehouse_management.com.warehouse_management.integration.customer.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsignmentCustomerIReq {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("cell_phone")
    private String cellPhone;

    @JsonProperty("password")
    private String password;

    @JsonProperty("email")
    private String email;

    @JsonProperty("address")
    private String address;

    // Cấp độ khách hàng
    @JsonProperty("customer_level_code")
    private String customerLevelCode;

    // Office được tạo cùng lúc với Customer
    @JsonProperty("office_name")
    private String officeName;

    @JsonProperty("office_code")
    private String officeCode;

    @JsonProperty("office_type_code")
    private String officeTypeCode;
}
