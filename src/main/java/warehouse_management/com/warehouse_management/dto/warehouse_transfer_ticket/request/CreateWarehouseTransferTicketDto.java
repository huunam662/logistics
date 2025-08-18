package warehouse_management.com.warehouse_management.dto.warehouse_transfer_ticket.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
public class CreateWarehouseTransferTicketDto {

    private String ticketCode;      // Số phiếu điều chuyển (không bắt buộc)
    private ObjectId originWarehouseId;     // Kho nguồn
    private ObjectId destinationWarehouseId; // Kho đích
    private Department stockInDepartment;   // Bộ phận nhập kho
    private Department stockOutDepartment;  // Bộ phận xuất kho
    private ShipUnitInfo shipUnitInfo;  //  Thông tin vận chuyển

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShipUnitInfo{
        private String fullName;    // Họ tên
        private String licensePlate;    // Biển số xe
        private String phone;   // Số điện thoại
        private String identityCode;    // Căn cước công dân
        private String reason;  //  Lý do điều chuyển
        private String shipMethod;  // Phương thưc vận chuyển
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Department{
        private String name; // Tên bộ phận
        private String address; // Địa chỉ
        private String phone;   // Số điện thoại
        private String position;    // Chức vụ / vị trí công tác
    }
}
