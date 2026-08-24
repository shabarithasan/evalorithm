package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.entity.ExamNotification;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.ExamNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam-notifications")
@RequiredArgsConstructor
@Tag(name = "Exam Notifications", description = "Exam notification endpoints")
public class ExamNotificationController {

    private final ExamNotificationService examNotificationService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get user notifications", description = "Get exam notifications for current user")
    public ResponseEntity<ApiResponse<List<ExamNotification>>> getUserNotifications() {
        Long userId = getCurrentUserId();
        List<ExamNotification> response = examNotificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", response));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount() {
        Long userId = getCurrentUserId();
        long count = examNotificationService.getUnreadCount(userId);
        Map<String, Long> data = new HashMap<>();
        data.put("unreadCount", count);
        return ResponseEntity.ok(ApiResponse.success("Unread count retrieved", data));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        examNotificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
