package warehouse_management.com.warehouse_management.dto.notification.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String body;
    
    @NotBlank(message = "To user username is required")
    private String toUserUsername;
}
