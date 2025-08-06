package warehouse_management.com.warehouse_management.module.warehouse.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class WarehouseUserRes {

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

    private WarehouseUserRes.User userManaged;

    @Data
    @NoArgsConstructor
    public static class User {

        private String id;
        private String username;
    }
}
