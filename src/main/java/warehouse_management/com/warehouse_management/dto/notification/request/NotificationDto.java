package warehouse_management.com.warehouse_management.dto.notification.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    private String id;
    private String title;
    private String body;
    private LocalDateTime time;
    private String createdByUsername;
    private String createdByFullName;
    private String toUserUsername;
    private Boolean unread;
}
