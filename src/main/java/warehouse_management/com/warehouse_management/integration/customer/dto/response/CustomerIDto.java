package warehouse_management.com.warehouse_management.integration.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerIDto {

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

    @JsonProperty("customer_level_id")
    private String customerLevelId;

    @JsonProperty("customer_level_name")
    private String customerLevelName;

    @JsonProperty("customer_level_note")
    private String customerLevelNote;
}