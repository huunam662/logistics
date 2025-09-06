package warehouse_management.com.warehouse_management.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AnaworkToken {
    @JsonProperty("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name")
    private String email;

    @JsonProperty("exp")
    private long expiration;

    @JsonProperty("iss")
    private String issuer;

    @JsonProperty("aud")
    private String audience;


}