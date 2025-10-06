package warehouse_management.com.warehouse_management.dto.notification;

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
    private LocalDateTime createdAt;
    
    // Created by user info
    private String createdById;
    private String createdByEmail;
    private String createdByFullName;
    
    // To user info
    private String toUserId;
    private String toUserEmail;
    private String toUserFullName;
    
    private Boolean unread;
}
