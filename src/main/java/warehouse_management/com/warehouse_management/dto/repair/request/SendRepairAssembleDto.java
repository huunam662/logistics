package warehouse_management.com.warehouse_management.dto.repair.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SendRepairAssembleDto {
    private String vehicleId;                 // Sản phẩm được bảo hành
    private String componentId;
    private LocalDate expectedCompletionDate;                   // Ngày dự kiến hoàn thành
}
