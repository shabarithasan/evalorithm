package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.ExamResultResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.StudentAnswerResponse;
import com.evalorithm.entity.StudentProfile;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.StudentProfileRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.ExamResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-results")
@RequiredArgsConstructor
@Tag(name = "Exam Results", description = "Exam results and evaluation endpoints")
public class ExamResultController {

    private final ExamResultService examResultService;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @GetMapping("/exam/{examId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get all results for exam", description = "Get paginated results for an exam (Admin/Faculty)")
    public ResponseEntity<ApiResponse<PageResponse<ExamResultResponse>>> getAllResultsForExam(
            @PathVariable Long examId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        PageResponse<ExamResultResponse> response = examResultService.getAllResultsForExam(examId, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Results retrieved", response));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student results", description = "Get all results for a student")
    public ResponseEntity<ApiResponse<PageResponse<ExamResultResponse>>> getStudentResults(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        PageResponse<ExamResultResponse> response = examResultService.getStudentResults(studentId, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Results retrieved", response));
    }

    @GetMapping("/exam/{examId}/student/{studentId}")
    @Operation(summary = "Get specific result", description = "Get student's result for a specific exam")
    public ResponseEntity<ApiResponse<ExamResultResponse>> getResult(@PathVariable Long examId,
                                                                      @PathVariable Long studentId) {
        ExamResultResponse response = examResultService.getResult(examId, studentId);
        return ResponseEntity.ok(ApiResponse.success("Result retrieved", response));
    }

    @GetMapping("/{resultId}/details")
    @Operation(summary = "Get result details", description = "Get question-wise breakdown of a result")
    public ResponseEntity<ApiResponse<List<StudentAnswerResponse>>> getResultDetails(@PathVariable Long resultId) {
        List<StudentAnswerResponse> response = examResultService.getResultDetails(resultId);
        return ResponseEntity.ok(ApiResponse.success("Result details retrieved", response));
    }

    @GetMapping("/exam/{examId}/export")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Export results", description = "Export exam results as CSV (Admin/Faculty)")
    public ResponseEntity<byte[]> exportResults(@PathVariable Long examId) {
        byte[] csvData = examResultService.exportResults(examId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exam_results_" + examId + ".csv")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(csvData);
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
