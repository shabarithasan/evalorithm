package com.evalorithm.controller;

import com.evalorithm.dto.request.StudentAnswerRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.LiveExamResponse;
import com.evalorithm.dto.response.SubmitExamResponse;
import com.evalorithm.entity.StudentProfile;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.StudentProfileRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.ExamTakingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam-session")
@RequiredArgsConstructor
@Tag(name = "Exam Taking", description = "Online examination session endpoints")
public class ExamTakingController {

    private final ExamTakingService examTakingService;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @PostMapping("/start")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Start exam", description = "Start an exam attempt (Student only)")
    public ResponseEntity<ApiResponse<LiveExamResponse>> startExam(@RequestParam Long examId,
                                                                    HttpServletRequest request) {
        Long userId = getCurrentUserId();
        StudentProfile student = getStudentProfile(userId);
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        LiveExamResponse response = examTakingService.startExam(examId, student.getId(), ip, userAgent);
        return ResponseEntity.ok(ApiResponse.success("Exam started", response));
    }

    @GetMapping("/{attemptId}/question/{index}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get exam question", description = "Get specific question during exam (Student only)")
    public ResponseEntity<ApiResponse<LiveExamResponse>> getExamQuestion(@PathVariable Long attemptId,
                                                                          @PathVariable int index) {
        LiveExamResponse response = examTakingService.getExamQuestion(attemptId, index);
        return ResponseEntity.ok(ApiResponse.success("Question retrieved", response));
    }

    @PostMapping("/{attemptId}/save-answer")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Save answer", description = "Save/update student answer during exam (Student only)")
    public ResponseEntity<ApiResponse<Void>> saveAnswer(@PathVariable Long attemptId,
                                                         @Valid @RequestBody StudentAnswerRequest request) {
        examTakingService.saveAnswer(attemptId, request);
        return ResponseEntity.ok(ApiResponse.success("Answer saved"));
    }

    @PostMapping("/{attemptId}/submit")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit exam", description = "Submit the exam attempt (Student only)")
    public ResponseEntity<ApiResponse<SubmitExamResponse>> submitExam(@PathVariable Long attemptId) {
        SubmitExamResponse response = examTakingService.submitExam(attemptId);
        return ResponseEntity.ok(ApiResponse.success("Exam submitted", response));
    }

    @PostMapping("/{attemptId}/resume")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Resume exam", description = "Resume a previously started exam (Student only)")
    public ResponseEntity<ApiResponse<LiveExamResponse>> resumeExam(@PathVariable Long attemptId) {
        LiveExamResponse response = examTakingService.resumeExam(attemptId);
        return ResponseEntity.ok(ApiResponse.success("Exam resumed", response));
    }

    @GetMapping("/{examId}/status")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get exam status", description = "Check exam attempt status (Student only)")
    public ResponseEntity<ApiResponse<LiveExamResponse>> getExamStatus(@PathVariable Long examId) {
        Long userId = getCurrentUserId();
        StudentProfile student = getStudentProfile(userId);
        LiveExamResponse response = examTakingService.getExamStatus(examId, student.getId());
        return ResponseEntity.ok(ApiResponse.success("Exam status retrieved", response));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }

    private StudentProfile getStudentProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", userId));
    }
}
