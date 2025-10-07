package warehouse_management.com.warehouse_management.integration.office.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOfficeFromWarehouseIReq {
    private String name;
    private String code;

    @JsonProperty("office_type_code")
    private String officeTypeCode;
}
