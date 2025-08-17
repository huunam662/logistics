package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;


@Data
@Document(collection = "audit_log")
public class AuditLog {

    @Id
    private ObjectId id;           // MongoDB document id
    private ObjectId userId;       // user_id
    private String action;         // action
    private LocalDateTime timestamp;     // timestamp
    private String status;         // optional
    private String errorMessage;   // optional

    // Constructor đầy đủ
    public AuditLog(ObjectId userId, String action, LocalDateTime timestamp, String status, String errorMessage) {
        this.userId = userId;
        this.action = action;
        this.timestamp = timestamp;
        this.status = status;
        this.errorMessage = errorMessage;
    }

}