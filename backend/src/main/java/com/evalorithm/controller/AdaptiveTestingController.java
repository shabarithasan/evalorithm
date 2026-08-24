package com.evalorithm.controller;

import com.evalorithm.dto.request.AdaptiveAnswerRequest;
import com.evalorithm.dto.request.AdaptiveSessionRequest;
import com.evalorithm.dto.response.*;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.AdaptiveTestingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/adaptive")
@RequiredArgsConstructor
@Tag(name = "Adaptive Testing", description = "Adaptive testing session management")
public class AdaptiveTestingController {

    private final AdaptiveTestingService adaptiveTestingService;
    private final UserRepository userRepository;

    @PostMapping("/start")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Start adaptive session", description = "Start a new adaptive testing session")
    public ResponseEntity<ApiResponse<AdaptiveSessionResponse>> startSession(
            @Valid @RequestBody AdaptiveSessionRequest request) {
        Long userId = getCurrentUserId();
        AdaptiveSessionResponse response = adaptiveTestingService.startAdaptiveSession(userId, request.getSubjectId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Session started", response));
    }

    @GetMapping("/{sessionId}/next")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get next question", description = "Get the next adaptive question")
    public ResponseEntity<ApiResponse<AdaptiveQuestionResponse>> getNextQuestion(@PathVariable Long sessionId) {
        AdaptiveQuestionResponse response = adaptiveTestingService.getNextQuestion(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Next question retrieved", response));
    }

    @PostMapping("/{sessionId}/answer")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Submit answer", description = "Submit answer and get next question")
    public ResponseEntity<ApiResponse<AdaptiveQuestionResponse>> submitAnswer(
            @PathVariable Long sessionId,
            @Valid @RequestBody AdaptiveAnswerRequest request) {
        AdaptiveQuestionResponse response = adaptiveTestingService.submitAnswer(sessionId, request);
        return ResponseEntity.ok(ApiResponse.success("Answer submitted", response));
    }

    @PostMapping("/{sessionId}/end")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "End session", description = "End the adaptive testing session")
    public ResponseEntity<ApiResponse<AdaptiveSessionResponse>> endSession(@PathVariable Long sessionId) {
        AdaptiveSessionResponse response = adaptiveTestingService.endSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session ended", response));
    }

    @GetMapping("/{sessionId}/history")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get session history", description = "Get difficulty change history of the session")
    public ResponseEntity<ApiResponse<List<QuestionDifficultyHistoryResponse>>> getSessionHistory(
            @PathVariable Long sessionId) {
        List<QuestionDifficultyHistoryResponse> response = adaptiveTestingService.getSessionHistory(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session history retrieved", response));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
