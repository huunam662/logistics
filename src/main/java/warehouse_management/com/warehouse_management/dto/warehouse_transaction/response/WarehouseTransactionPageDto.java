package warehouse_management.com.warehouse_management.dto.warehouse_transaction.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;

@Data
public class WarehouseTransactionPageDto {
    private ObjectId id;
    private String ticketCode;
    private String title;
    private String reason;
    private String status;
    private String tranType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedAt;   // Ngày duyệt
    private String requesterName;               // Người tạo yêu cầu
}
