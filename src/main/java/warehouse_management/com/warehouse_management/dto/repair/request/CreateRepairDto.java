package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateRepairDto {
    private String vehicleId;                 // Sản phẩm được bảo hành
    private String componentId;
    private String repairType;
    private LocalDate expectedCompletionDate;                   // Ngày dự kiến hoàn thành
}
