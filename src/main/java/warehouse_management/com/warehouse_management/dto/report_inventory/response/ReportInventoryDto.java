package warehouse_management.com.warehouse_management.dto.report_inventory.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportInventoryDto {
    private String poNumber;
    private String model;
    private String agent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime loadToWarehouseDate;
    private String reportType;
    private Long totalVehicle;
    private Long totalAccessory;
    private Long totalSparePart;
}
