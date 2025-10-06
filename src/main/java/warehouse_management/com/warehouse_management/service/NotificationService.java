package warehouse_management.com.warehouse_management.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import warehouse_management.com.warehouse_management.dto.notification.CreateNotificationRequest;
import warehouse_management.com.warehouse_management.dto.notification.NotificationDto;
import warehouse_management.com.warehouse_management.exceptions.LogicErrException;
import warehouse_management.com.warehouse_management.mapper.NotificationMapper;
import warehouse_management.com.warehouse_management.model.Notification;
import warehouse_management.com.warehouse_management.model.UserInfo;
import warehouse_management.com.warehouse_management.repository.NotificationRepository;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    
    /**
     * Get all unread notifications for a specific user
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(String userId) {
        List<Notification> notifications = notificationRepository
                .findByToUserIdAndUnreadOrderByTimeDesc(userId, true);
        
        log.info("Retrieved {} unread notifications for user ID: {}", notifications.size(), userId);
        
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get count of unread notifications
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notificationRepository.countByToUserIdAndUnread(userId, true);
    }
    
    /**
     * Mark notifications as read
     */
    @Transactional
    public void markAsRead(List<String> notificationIds, String currentUserId) {
        List<Notification> notifications = notificationRepository.findAllById(notificationIds);
        
        if (notifications.isEmpty()) {
            throw LogicErrException.of("No notifications found with provided IDs");
        }
        
        // Verify that all notifications belong to the current user
        notifications.forEach(notification -> {
            if (notification.getToUser() == null || !notification.getToUser().getId().equals(currentUserId)) {
                throw new IllegalArgumentException("Cannot mark notification as read - not owned by current user");
            }
            notification.setUnread(false);
        });
        
        notificationRepository.saveAll(notifications);
        
        log.info("Marked {} notifications as read for user ID: {}", notifications.size(), currentUserId);
    }
    
    /**
     * Create a new notification (for other services to call)
     * @param request The notification request containing toUser info
     * @param currentUser The current authenticated user (can be null for system notifications)
     */
    @Transactional
    public NotificationDto createNotification(CreateNotificationRequest request, CustomUserDetail currentUser) {
        UserInfo toUser = UserInfo.builder()
                .id(request.getToUserId())
                .email(request.getToUserEmail())
                .fullName(request.getToUserFullName())
                .build();
        
        UserInfo createdBy = null;
        if (currentUser != null) {
            createdBy = UserInfo.builder()
                    .id(currentUser.getId())
                    .email(currentUser.getEmail())
                    .fullName(currentUser.getFullName())
                    .permissions(currentUser.getPermisions())
                    .build();
        }
        
        Notification notification = Notification.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .createdAt(LocalDateTime.now())
                .toUser(toUser)
                .createdBy(createdBy)
                .unread(true)
                .build();
        
        notification = notificationRepository.save(notification);
        
        log.info("Created notification for user ID: {} with title: {}", toUser.getId(), request.getTitle());
        
        return notificationMapper.toDto(notification);
    }
    
    /**
     * Get all notifications for a user (both read and unread)
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications(String userId) {
        List<Notification> notifications = notificationRepository.findByToUserIdOrderByTimeDesc(userId);
        
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }
}