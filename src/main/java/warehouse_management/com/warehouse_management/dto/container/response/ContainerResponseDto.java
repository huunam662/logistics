package warehouse_management.com.warehouse_management.dto.container.response;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Data
public class ContainerResponseDto {
    private String id;

    private String containerCode;       // Mã định danh duy nhất
    private String containerStatus;     // EMPTY, LOADING, IN_TRANSIT, COMPLETED

    private LocalDateTime departureDate;  // Ngày khởi hành
    private LocalDateTime arrivalDate;    // Ngày đến nơi

    private String status;
    private String note;

    private ObjectId fromWareHouseId;
    private ObjectId toWarehouseId;
}
