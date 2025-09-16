package warehouse_management.com.warehouse_management.dto.report_inventory.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReportInventoryDto {
    private String poNumber;
    private String model;
    private String agent;
    private String reportType;
    private String inventoryType;
    private String containerCode;
    private String containerStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime loadToWarehouseDate;
    private Long totalVehicle;
    private Long totalAccessory;
    private Long totalSparePart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime departureDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime arrivalDate;
    private Integer daysLate;
    private String deliveryOrderCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime deliveryDate;
    private String customerName;
}
