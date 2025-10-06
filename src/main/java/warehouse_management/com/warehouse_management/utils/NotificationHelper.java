package warehouse_management.com.warehouse_management.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import warehouse_management.com.warehouse_management.dto.notification.CreateNotificationRequest;
import warehouse_management.com.warehouse_management.dto.notification.NotificationDto;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;
import warehouse_management.com.warehouse_management.service.NotificationService;

/**
 * Helper utility for creating notifications from other services
 */
@Component
@RequiredArgsConstructor
public class NotificationHelper {
    
    private final NotificationService notificationService;
    
    /**
     * Send a notification to a user
     * 
     * @param toUserId ID of the user to receive notification
     * @param toUserEmail Email of the user to receive notification
     * @param toUserFullName Full name of the user to receive notification
     * @param title Notification title
     * @param body Notification body/content
     * @param currentUser Current authenticated user (can be null for system notifications)
     * @return Created notification DTO
     */
    public NotificationDto sendNotification(
            String toUserId,
            String toUserEmail,
            String toUserFullName,
            String title,
            String body,
            CustomUserDetail currentUser) {
        
        CreateNotificationRequest request = CreateNotificationRequest.builder()
                .toUserId(toUserId)
                .toUserEmail(toUserEmail)
                .toUserFullName(toUserFullName)
                .title(title)
                .body(body)
                .build();
        
        return notificationService.createNotification(request, currentUser);
    }
    
    /**
     * Send a notification to a user (simplified version)
     * 
     * @param toUserId ID of the user to receive notification
     * @param toUserEmail Email of the user to receive notification
     * @param title Notification title
     * @param body Notification body/content
     * @param currentUser Current authenticated user (can be null for system notifications)
     * @return Created notification DTO
     */
    public NotificationDto sendNotification(
            String toUserId,
            String toUserEmail,
            String title,
            String body,
            CustomUserDetail currentUser) {
        
        return sendNotification(toUserId, toUserEmail, null, title, body, currentUser);
    }
}
