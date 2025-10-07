package warehouse_management.com.warehouse_management.integration.office.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfficeDto {

    private String id;
    private String name;

    @JsonProperty("full_address")
    private String fullAddress;

    private double radius;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;


    @JsonProperty("modified_at")
    private LocalDateTime modifiedAt;


    @JsonProperty("is_office")
    private boolean isOffice;

    private Double longitude;
    private Double latitude;

    @JsonProperty("office_type_id")
    private String officeTypeId;

    private String code;
}
