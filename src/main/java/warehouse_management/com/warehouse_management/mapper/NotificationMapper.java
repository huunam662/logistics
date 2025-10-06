package warehouse_management.com.warehouse_management.mapper;

import org.springframework.stereotype.Component;

import warehouse_management.com.warehouse_management.dto.notification.NotificationDto;
import warehouse_management.com.warehouse_management.model.Notification;
import warehouse_management.com.warehouse_management.model.UserInfo;

@Component
public class NotificationMapper {
    
    public NotificationDto toDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        
        NotificationDto.NotificationDtoBuilder builder = NotificationDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .body(notification.getBody())
                .createdAt(notification.getCreatedAt())
                .unread(notification.getUnread());
        
        // Map createdBy user info
        if (notification.getCreatedBy() != null) {
            UserInfo createdBy = notification.getCreatedBy();
            builder.createdById(createdBy.getId())
                   .createdByEmail(createdBy.getEmail())
                   .createdByFullName(createdBy.getFullName());
        }
        
        // Map toUser info
        if (notification.getToUser() != null) {
            UserInfo toUser = notification.getToUser();
            builder.toUserId(toUser.getId())
                   .toUserEmail(toUser.getEmail())
                   .toUserFullName(toUser.getFullName());
        }
        
        return builder.build();
    }
}