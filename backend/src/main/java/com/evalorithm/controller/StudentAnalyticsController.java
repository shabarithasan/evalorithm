package com.evalorithm.controller;

import com.evalorithm.dto.response.*;
import com.evalorithm.service.StudentAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics/students")
@RequiredArgsConstructor
@Tag(name = "Student Analytics", description = "Student performance analytics endpoints")
public class StudentAnalyticsController {

    private final StudentAnalyticsService studentAnalyticsService;

    @GetMapping("/{studentId}/dashboard")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get student dashboard", description = "Get student analytics dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStudentDashboard(@PathVariable Long studentId) {
        Map<String, Object> response = studentAnalyticsService.getStudentDashboard(studentId);
        return ResponseEntity.ok(ApiResponse.success("Dashboard retrieved", response));
    }

    @GetMapping("/{studentId}/subjects")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get subject performance", description = "Get performance across all subjects")
    public ResponseEntity<ApiResponse<List<SubjectPerformanceItem>>> getSubjectPerformance(
            @PathVariable Long studentId) {
        List<SubjectPerformanceItem> response = studentAnalyticsService.getSubjectPerformance(studentId);
        return ResponseEntity.ok(ApiResponse.success("Subject performance retrieved", response));
    }

    @GetMapping("/{studentId}/subjects/{subjectId}/units")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get unit performance", description = "Get unit-level performance for a subject")
    public ResponseEntity<ApiResponse<List<UnitPerformanceItem>>> getUnitPerformance(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        List<UnitPerformanceItem> response = studentAnalyticsService.getUnitPerformance(studentId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Unit performance retrieved", response));
    }

    @GetMapping("/{studentId}/subjects/{subjectId}/topics")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get topic performance", description = "Get topic-level performance for a subject")
    public ResponseEntity<ApiResponse<List<TopicPerformanceItem>>> getTopicPerformance(
            @PathVariable Long studentId,
            @PathVariable Long subjectId) {
        List<TopicPerformanceItem> response = studentAnalyticsService.getTopicPerformance(studentId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Topic performance retrieved", response));
    }

    @GetMapping("/{studentId}/difficulty")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get difficulty performance", description = "Get performance by difficulty level")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getDifficultyPerformance(@PathVariable Long studentId) {
        Map<String, Double> response = studentAnalyticsService.getDifficultyPerformance(studentId);
        return ResponseEntity.ok(ApiResponse.success("Difficulty performance retrieved", response));
    }

    @GetMapping("/{studentId}/accuracy-trend")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get accuracy trend", description = "Get accuracy over time for line chart")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAccuracyOverTime(
            @PathVariable Long studentId) {
        List<Map<String, Object>> response = studentAnalyticsService.getAccuracyOverTime(studentId);
        return ResponseEntity.ok(ApiResponse.success("Accuracy trend retrieved", response));
    }

    @PostMapping("/{studentId}/calculate")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Calculate analytics", description = "Recalculate student analytics for a subject")
    public ResponseEntity<ApiResponse<StudentAnalyticsResponse>> calculateAnalytics(
            @PathVariable Long studentId,
            @RequestParam Long subjectId) {
        StudentAnalyticsResponse response = studentAnalyticsService.calculateStudentAnalytics(studentId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Analytics calculated", response));
    }
}
