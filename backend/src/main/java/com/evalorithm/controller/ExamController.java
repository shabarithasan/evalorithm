package com.evalorithm.controller;

import com.evalorithm.dto.request.ExamQuestionRequest;
import com.evalorithm.dto.request.ExamRequest;
import com.evalorithm.dto.response.*;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exams")
@RequiredArgsConstructor
@Tag(name = "Exams", description = "Exam management endpoints")
public class ExamController {

    private final ExamService examService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all exams", description = "Get paginated and filtered list of exams")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String examType,
            @RequestParam(required = false) Long departmentId) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        PageResponse<ExamResponse> response = examService.getAllExams(pageRequest, search, status, examType, departmentId);
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exam by ID")
    public ResponseEntity<ApiResponse<ExamDetailResponse>> getById(@PathVariable Long id) {
        ExamDetailResponse response = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.success("Exam retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Create exam", description = "Create a new exam (Admin/Faculty)")
    public ResponseEntity<ApiResponse<ExamResponse>> create(@Valid @RequestBody ExamRequest request) {
        Long userId = getCurrentUserId();
        ExamResponse response = examService.createExam(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Update exam", description = "Update an existing exam (Admin/Faculty)")
    public ResponseEntity<ApiResponse<ExamResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody ExamRequest request) {
        ExamResponse response = examService.updateExam(id, request);
        return ResponseEntity.ok(ApiResponse.success("Exam updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete exam", description = "Delete an exam (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam deleted"));
    }

    @PostMapping("/{id}/clone")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Clone exam", description = "Clone an exam with all settings (Admin/Faculty)")
    public ResponseEntity<ApiResponse<ExamResponse>> clone(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ExamResponse response = examService.cloneExam(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Exam cloned", response));
    }

    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Publish exam", description = "Publish an exam (Admin only)")
    public ResponseEntity<ApiResponse<ExamResponse>> publish(@PathVariable Long id) {
        ExamResponse response = examService.publishExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam published", response));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Archive exam", description = "Archive an exam (Admin only)")
    public ResponseEntity<ApiResponse<ExamResponse>> archive(@PathVariable Long id) {
        ExamResponse response = examService.archiveExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam archived", response));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cancel exam", description = "Cancel an exam (Admin only)")
    public ResponseEntity<ApiResponse<ExamResponse>> cancel(@PathVariable Long id) {
        ExamResponse response = examService.cancelExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam cancelled", response));
    }

    @PostMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Add questions to exam", description = "Add questions to an exam (Admin/Faculty)")
    public ResponseEntity<ApiResponse<Void>> addQuestions(@PathVariable Long id,
                                                          @Valid @RequestBody List<ExamQuestionRequest> questions) {
        examService.addQuestionsToExam(id, questions);
        return ResponseEntity.ok(ApiResponse.success("Questions added to exam"));
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Remove question from exam")
    public ResponseEntity<ApiResponse<Void>> removeQuestion(@PathVariable Long id,
                                                             @PathVariable Long questionId) {
        examService.removeQuestionFromExam(id, questionId);
        return ResponseEntity.ok(ApiResponse.success("Question removed from exam"));
    }

    @PostMapping("/{id}/assign-students")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Assign students to exam")
    public ResponseEntity<ApiResponse<Void>> assignStudents(@PathVariable Long id,
                                                             @RequestBody List<Long> studentIds) {
        examService.assignStudents(id, studentIds);
        return ResponseEntity.ok(ApiResponse.success("Students assigned"));
    }

    @PostMapping("/{id}/unassign-students")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Unassign students from exam")
    public ResponseEntity<ApiResponse<Void>> unassignStudents(@PathVariable Long id,
                                                               @RequestBody List<Long> studentIds) {
        examService.unassignStudents(id, studentIds);
        return ResponseEntity.ok(ApiResponse.success("Students unassigned"));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get exam dashboard", description = "Get exam statistics dashboard (Admin/Faculty)")
    public ResponseEntity<ApiResponse<ExamDashboardResponse>> getDashboard() {
        ExamDashboardResponse response = examService.getExamDashboard();
        return ResponseEntity.ok(ApiResponse.success("Dashboard retrieved", response));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
