package warehouse_management.com.warehouse_management.dto.client.response;

import lombok.Data;

@Data
public class ClientDto {
    private String id;
    private String name;
    private String customerCode;
    private String address;
    private String email;
}
