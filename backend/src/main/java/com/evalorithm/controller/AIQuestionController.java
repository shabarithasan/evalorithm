package com.evalorithm.controller;

import com.evalorithm.dto.request.AIQuestionGenerateRequest;
import com.evalorithm.dto.response.AIQuestionResponse;
import com.evalorithm.dto.response.AIDashboardResponse;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.AIQuestionGeneratorService;
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
@RequestMapping("/ai/questions")
@RequiredArgsConstructor
@Tag(name = "AI Questions", description = "AI question generation and management endpoints")
public class AIQuestionController {

    private final AIQuestionGeneratorService aiQuestionGeneratorService;
    private final UserRepository userRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Generate AI questions", description = "Generate questions using template-based AI engine")
    public ResponseEntity<ApiResponse<List<AIQuestionResponse>>> generateQuestions(
            @Valid @RequestBody AIQuestionGenerateRequest request) {
        Long userId = getCurrentUserId();
        List<AIQuestionResponse> response = aiQuestionGeneratorService.generateQuestions(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Questions generated successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get generated questions", description = "Get paginated list of AI-generated questions")
    public ResponseEntity<ApiResponse<PageResponse<AIQuestionResponse>>> getGeneratedQuestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long subjectId) {
        Long userId = getCurrentUserId();
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<AIQuestionResponse> response = aiQuestionGeneratorService
                .getGeneratedQuestions(pageRequest, userId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Questions retrieved", response));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve AI question", description = "Approve an AI-generated question (Admin only)")
    public ResponseEntity<ApiResponse<AIQuestionResponse>> approveQuestion(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        AIQuestionResponse response = aiQuestionGeneratorService.approveQuestion(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Question approved", response));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reject AI question", description = "Reject an AI-generated question (Admin only)")
    public ResponseEntity<ApiResponse<AIQuestionResponse>> rejectQuestion(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        AIQuestionResponse response = aiQuestionGeneratorService.rejectQuestion(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Question rejected", response));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get AI dashboard", description = "Get AI question generation statistics")
    public ResponseEntity<ApiResponse<AIDashboardResponse>> getAIDashboard() {
        AIDashboardResponse response = aiQuestionGeneratorService.getAIDashboard();
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
