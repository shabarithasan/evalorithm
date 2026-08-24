package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.ExamReportResponse;
import com.evalorithm.service.ExamReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exam-reports")
@RequiredArgsConstructor
@Tag(name = "Exam Reports", description = "Exam reporting and analytics endpoints")
public class ExamReportController {

    private final ExamReportService examReportService;

    @GetMapping("/exam/{examId}/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get exam summary", description = "Get overall exam summary statistics (Admin/Faculty)")
    public ResponseEntity<ApiResponse<ExamReportResponse>> getExamSummary(@PathVariable Long examId) {
        ExamReportResponse response = examReportService.getExamSummary(examId);
        return ResponseEntity.ok(ApiResponse.success("Exam summary retrieved", response));
    }

    @GetMapping("/exam/{examId}/question-wise")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get question-wise report", description = "Get per-question statistics (Admin/Faculty)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestionWiseReport(@PathVariable Long examId) {
        List<Map<String, Object>> response = examReportService.getQuestionWiseReport(examId);
        return ResponseEntity.ok(ApiResponse.success("Question-wise report retrieved", response));
    }

    @GetMapping("/exam/{examId}/student-wise")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get student-wise report", description = "Get per-student statistics (Admin/Faculty)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getStudentWiseReport(@PathVariable Long examId) {
        List<Map<String, Object>> response = examReportService.getStudentWiseReport(examId);
        return ResponseEntity.ok(ApiResponse.success("Student-wise report retrieved", response));
    }
}
