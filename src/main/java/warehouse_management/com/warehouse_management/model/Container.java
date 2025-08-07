package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse_management.com.warehouse_management.enumerate.ContainerStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "container")
public class Container {

    @Id
    private ObjectId id;  // _id – Khóa chính

    private String containerCode;       // Mã định danh duy nhất
    private String containerStatus;     // EMPTY, LOADING, IN_TRANSIT, COMPLETED

    private LocalDateTime departureDate;  // Ngày khởi hành
    private LocalDateTime arrivalDate;    // Ngày đến nơi

    private String note;                // Ghi chú

    private ObjectId fromWareHouseId;     // Tham chiếu đến _id kho đi
    private ObjectId toWarehouseId;       // Tham chiếu đến _id kho đến

    private ObjectId createdBy;
    private ObjectId updatedBy;
    private ObjectId deletedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public ContainerStatus getContainerStatus() {
        return containerStatus == null ? null : ContainerStatus.fromId(containerStatus);
    }

    public void setContainerStatus(ContainerStatus containerStatus) {
        this.containerStatus = containerStatus == null ? null : containerStatus.getId();
    }
}