package warehouse_management.com.warehouse_management.dto.warranty.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Data
public class WarrantyTransactionResponseDTO {
    private ObjectId id;
    private ObjectId warrantyId;        // Phiếu bảo hành cha
    private String sparePartWarranty;   // Bộ phận cần bảo hành
    private String reason;              // Lý do bảo hành
    private String createByName;        // Fullname của người tạo phiếu bảo hành
    private String updateByName;        // Fullname của người cập nhật phiếu bảo hành
    private Boolean isCompleted;        // Trạng thái hoàn thành của phiếu

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
