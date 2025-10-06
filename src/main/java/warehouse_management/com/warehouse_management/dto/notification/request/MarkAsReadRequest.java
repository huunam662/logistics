package warehouse_management.com.warehouse_management.dto.notification.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarkAsReadRequest {
    
    @NotEmpty(message = "Notification IDs cannot be empty")
    private List<String> notificationIds;
}
