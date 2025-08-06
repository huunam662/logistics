package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import warehouse_management.com.warehouse_management.enumerate.ActiveStatus;
import warehouse_management.com.warehouse_management.enumerate.WarehouseType;

@Document(collection = "warehouse")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    private ObjectId id;        // _id

    private String name;       // Tên kho (bắt buộc)
    private String code;       // Mã kho duy nhất (bắt buộc)
    private String type;       // PRODUCTION, DEPARTURE, etc. (bắt buộc)
    private String status;     // ACTIVE, INACTIVE (bắt buộc)

    private String address;    // Không bắt buộc
    private String area;       // Không bắt buộc

    private ObjectId managedBy; // Tham chiếu đến users._id

    private String note;       // Ghi chú thêm

    public ActiveStatus getStatus() {
        return status == null ? null : ActiveStatus.fromId(status);
    }

    public void setStatus(ActiveStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public WarehouseType getType() {
        return type == null ? null : WarehouseType.fromId(type);
    }

    public void setStatus(WarehouseType warehouseType) {
        this.type = warehouseType == null ? null : warehouseType.getId();
    }

}