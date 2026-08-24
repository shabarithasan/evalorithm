package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.FacultyAnalyticsResponse;
import com.evalorithm.service.FacultyAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics/faculty")
@RequiredArgsConstructor
@Tag(name = "Faculty Analytics", description = "Faculty analytics endpoints")
@PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
public class FacultyAnalyticsController {

    private final FacultyAnalyticsService facultyAnalyticsService;

    @GetMapping("/{facultyId}/dashboard")
    @Operation(summary = "Get faculty analytics", description = "Get faculty analytics dashboard")
    public ResponseEntity<ApiResponse<FacultyAnalyticsResponse>> getFacultyDashboard(@PathVariable Long facultyId) {
        FacultyAnalyticsResponse response = facultyAnalyticsService.calculateFacultyAnalytics(facultyId);
        return ResponseEntity.ok(ApiResponse.success("Faculty analytics retrieved", response));
    }

    @GetMapping("/{facultyId}/subjects/{subjectId}/class-performance")
    @Operation(summary = "Get class performance", description = "Get class performance for a subject")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getClassPerformance(
            @PathVariable Long facultyId,
            @PathVariable Long subjectId) {
        Map<String, Object> response = facultyAnalyticsService.getClassPerformance(facultyId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Class performance retrieved", response));
    }

    @GetMapping("/{facultyId}/subjects/{subjectId}/top-performers")
    @Operation(summary = "Get top performers", description = "Get top performing students")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTopPerformers(
            @PathVariable Long facultyId,
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> response = facultyAnalyticsService.getTopPerformers(facultyId, subjectId, limit);
        return ResponseEntity.ok(ApiResponse.success("Top performers retrieved", response));
    }

    @GetMapping("/{facultyId}/subjects/{subjectId}/low-performers")
    @Operation(summary = "Get low performers", description = "Get students who need improvement")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLowPerformers(
            @PathVariable Long facultyId,
            @PathVariable Long subjectId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> response = facultyAnalyticsService.getLowPerformers(facultyId, subjectId, limit);
        return ResponseEntity.ok(ApiResponse.success("Low performers retrieved", response));
    }

    @GetMapping("/{facultyId}/subject-analysis")
    @Operation(summary = "Get subject analysis", description = "Get per-subject analytics")
    public ResponseEntity<ApiResponse<List<FacultyAnalyticsResponse>>> getSubjectAnalysis(
            @PathVariable Long facultyId) {
        List<FacultyAnalyticsResponse> response = facultyAnalyticsService.getSubjectAnalysis(facultyId);
        return ResponseEntity.ok(ApiResponse.success("Subject analysis retrieved", response));
    }
}
