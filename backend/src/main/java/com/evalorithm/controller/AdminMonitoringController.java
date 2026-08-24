package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.service.AdminMonitoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/monitoring")
@RequiredArgsConstructor
@Tag(name = "Admin Monitoring", description = "System monitoring and health endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMonitoringController {

    private final AdminMonitoringService adminMonitoringService;

    @GetMapping("/online")
    @Operation(summary = "Get online users count")
    public ResponseEntity<ApiResponse<Long>> getOnlineUsers() {
        Long count = adminMonitoringService.getOnlineUsers();
        return ResponseEntity.ok(ApiResponse.success("Online users retrieved", count));
    }

    @GetMapping("/health")
    @Operation(summary = "Get system health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSystemHealth() {
        Map<String, Object> health = adminMonitoringService.getSystemHealth();
        return ResponseEntity.ok(ApiResponse.success("System health retrieved", health));
    }

    @GetMapping("/api-health")
    @Operation(summary = "Get API health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAPIHealth() {
        Map<String, Object> health = adminMonitoringService.getAPIHealth();
        return ResponseEntity.ok(ApiResponse.success("API health retrieved", health));
    }

    @GetMapping("/db-health")
    @Operation(summary = "Get database health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDatabaseHealth() {
        Map<String, Object> health = adminMonitoringService.getDatabaseHealth();
        return ResponseEntity.ok(ApiResponse.success("Database health retrieved", health));
    }

    @GetMapping("/storage")
    @Operation(summary = "Get storage usage")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStorageUsage() {
        Map<String, Object> usage = adminMonitoringService.getStorageUsage();
        return ResponseEntity.ok(ApiResponse.success("Storage usage retrieved", usage));
    }
}
