package warehouse_management.com.warehouse_management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Notification model
 * Uses embedded UserInfo based on CustomUserDetail structure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    private String title;
    
    private String body;
    
    private LocalDateTime createdAt;
    
    private UserInfo createdBy;
    
    private UserInfo toUser;
    
    @Builder.Default
    private Boolean unread = true;
}
