package warehouse_management.com.warehouse_management.dto.container.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ContainerResponseDto {
    private String id;

    private String containerCode;       // Mã định danh duy nhất
    private String containerStatus;     // EMPTY, LOADING, IN_TRANSIT, COMPLETED

    
    private LocalDateTime departureDate;  // Ngày khởi hành
    
    private LocalDateTime arrivalDate;    // Ngày đến nơi
    
    private LocalDateTime completionDate; // Ngày hoàn tất

    private String note;

    private WarehouseInfo fromWarehouse;
    private WarehouseInfo toWarehouse;

    private BigDecimal totalAmounts;

    @Data
    public static class WarehouseInfo {
        private String id;
        private String name;
        private String code;
    }
}
