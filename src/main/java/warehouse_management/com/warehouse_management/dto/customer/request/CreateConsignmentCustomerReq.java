package warehouse_management.com.warehouse_management.dto.customer.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConsignmentCustomerReq {


    private String firstName;


    private String lastName;


    private String cellPhone;


    private String password;


    private String email;


    private String address;

    // Cấp độ khách hàng

    private String customerLevelCode;


}
