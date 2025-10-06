package warehouse_management.com.warehouse_management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import warehouse_management.com.warehouse_management.dto.ApiResponse;
import warehouse_management.com.warehouse_management.dto.notification.CreateNotificationRequest;
import warehouse_management.com.warehouse_management.dto.notification.MarkAsReadRequest;
import warehouse_management.com.warehouse_management.dto.notification.NotificationDto;
import warehouse_management.com.warehouse_management.security.CustomUserDetail;
import warehouse_management.com.warehouse_management.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification management APIs")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications", description = "Get all unread notifications for the authenticated user")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(Authentication authentication) {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        System.out.println("User ID: " + userDetail.getId());
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userDetail.getId());
        
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @GetMapping("/unread/count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication authentication) {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        long count = notificationService.getUnreadCount(userDetail.getId());
        
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    @GetMapping
    @Operation(summary = "Get all notifications", description = "Get all notifications (read and unread) for the authenticated user")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getAllNotifications(Authentication authentication) {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        List<NotificationDto> notifications = notificationService.getAllNotifications(userDetail.getId());
        
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }
    
    @PutMapping("/mark-as-read")
    @Operation(summary = "Mark notifications as read", description = "Mark one or multiple notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @Valid @RequestBody MarkAsReadRequest request,
            Authentication authentication) {
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        notificationService.markAsRead(request.getNotificationIds(), userDetail.getId());
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    @PostMapping
    @Operation(summary = "Create notification", description = "Create a new notification (for internal service calls)")
    public ResponseEntity<ApiResponse<NotificationDto>> createNotification(
            @Valid @RequestBody CreateNotificationRequest request,
            Authentication authentication) {
        CustomUserDetail currentUser = authentication != null ? (CustomUserDetail) authentication.getPrincipal() : null;
        NotificationDto notification = notificationService.createNotification(request, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(notification));
    }
}