package warehouse_management.com.warehouse_management.integration.customer.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerDto {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("first_name")
    private String firstName;
    
    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("is_customer")
    private Boolean isCustomer;
    
    @JsonProperty("cell_phone")
    private String cellPhone;
    
    @JsonProperty("customer_level")
    private String customerLevel;
}

