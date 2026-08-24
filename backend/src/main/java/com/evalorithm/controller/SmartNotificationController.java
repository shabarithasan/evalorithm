package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.SmartNotificationResponse;
import com.evalorithm.service.SmartNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/smart-notifications")
@RequiredArgsConstructor
@Tag(name = "Smart Notifications", description = "AI-powered smart notification endpoints")
public class SmartNotificationController {

    private final SmartNotificationService smartNotificationService;

    @PostMapping("/generate/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Generate notifications", description = "Generate smart notifications based on performance")
    public ResponseEntity<ApiResponse<List<SmartNotificationResponse>>> generateNotifications(
            @PathVariable Long studentId) {
        List<SmartNotificationResponse> response = smartNotificationService.generateSmartNotifications(studentId);
        return ResponseEntity.ok(ApiResponse.success("Notifications generated", response));
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get notifications", description = "Get smart notifications for a student")
    public ResponseEntity<ApiResponse<List<SmartNotificationResponse>>> getNotifications(
            @PathVariable Long studentId) {
        List<SmartNotificationResponse> response = smartNotificationService.getSmartNotifications(studentId);
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved", response));
    }
}
