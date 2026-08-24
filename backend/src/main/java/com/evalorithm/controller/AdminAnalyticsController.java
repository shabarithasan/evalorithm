package com.evalorithm.controller;

import com.evalorithm.dto.response.AdminAnalyticsResponse;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.service.AdminAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Analytics", description = "Admin analytics endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    private final AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/overview")
    @Operation(summary = "Get overall statistics", description = "Get system-wide statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallStatistics() {
        Map<String, Object> response = adminAnalyticsService.getOverallStatistics();
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved", response));
    }

    @GetMapping("/departments")
    @Operation(summary = "Get department performance", description = "Get per-department analytics")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDepartmentPerformance() {
        List<Map<String, Object>> response = adminAnalyticsService.getDepartmentPerformance();
        return ResponseEntity.ok(ApiResponse.success("Department performance retrieved", response));
    }

    @GetMapping("/student-growth")
    @Operation(summary = "Get student growth", description = "Get monthly student registrations for last 12 months")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStudentGrowth() {
        List<Map<String, Object>> response = adminAnalyticsService.getStudentGrowth();
        return ResponseEntity.ok(ApiResponse.success("Student growth retrieved", response));
    }

    @GetMapping("/top-performers")
    @Operation(summary = "Get top performers", description = "Get top performing students globally")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopPerformers(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> response = adminAnalyticsService.getTopPerformersGlobal(limit);
        return ResponseEntity.ok(ApiResponse.success("Top performers retrieved", response));
    }

    @GetMapping("/low-performers")
    @Operation(summary = "Get low performers", description = "Get students at risk globally")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLowPerformers(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> response = adminAnalyticsService.getLowPerformersGlobal(limit);
        return ResponseEntity.ok(ApiResponse.success("Low performers retrieved", response));
    }

    @GetMapping("/faculty-performance")
    @Operation(summary = "Get faculty performance", description = "Get per-faculty statistics")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFacultyPerformance() {
        List<Map<String, Object>> response = adminAnalyticsService.getFacultyPerformance();
        return ResponseEntity.ok(ApiResponse.success("Faculty performance retrieved", response));
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard", description = "Get comprehensive admin dashboard data")
    public ResponseEntity<ApiResponse<AdminAnalyticsResponse>> getAdminDashboard() {
        AdminAnalyticsResponse response = adminAnalyticsService.getAdminDashboard();
        return ResponseEntity.ok(ApiResponse.success("Admin dashboard retrieved", response));
    }
}
