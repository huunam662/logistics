package warehouse_management.com.warehouse_management.dto.warehouse.response;

import lombok.Data;

import java.time.LocalDateTime;

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
    private LocalDateTime deletedAt;
    private String deletedBy;
    private LocalDateTime createdAt;
}
