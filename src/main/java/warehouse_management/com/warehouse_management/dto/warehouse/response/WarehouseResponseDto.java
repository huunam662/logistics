package warehouse_management.com.warehouse_management.dto.warehouse.response;

import lombok.Data;

@Data
public class WarehouseResponseDto {
    private String id;
    private String name;
    private String code;
    private String type;
    private String status;
    private String address;
    private String managedById;
    private String note;
    private String deletedAt;
    private String deletedBy;
    private String createdAt;
}
