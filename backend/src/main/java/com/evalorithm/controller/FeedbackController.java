package com.evalorithm.controller;

import com.evalorithm.dto.request.FeedbackRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.FeedbackResponse;
import com.evalorithm.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "Feedback management endpoints")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping
    @Operation(summary = "Submit feedback")
    public ResponseEntity<ApiResponse<FeedbackResponse>> submit(
            @Valid @RequestBody FeedbackRequest request,
            @RequestParam Long fromUserId) {
        FeedbackResponse response = feedbackService.submitFeedback(request, fromUserId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Feedback submitted", response));
    }

    @GetMapping("/received/{userId}")
    @Operation(summary = "Get feedback received by user")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getReceived(@PathVariable Long userId) {
        List<FeedbackResponse> response = feedbackService.getFeedbackForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Feedback retrieved", response));
    }

    @GetMapping("/given/{userId}")
    @Operation(summary = "Get feedback given by user")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getGiven(@PathVariable Long userId) {
        List<FeedbackResponse> response = feedbackService.getFeedbackByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Feedback retrieved", response));
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get feedback for subject")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getSubjectFeedback(@PathVariable Long subjectId) {
        List<FeedbackResponse> response = feedbackService.getSubjectFeedback(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Subject feedback retrieved", response));
    }

    @GetMapping("/rating/{subjectId}")
    @Operation(summary = "Get average rating for subject")
    public ResponseEntity<ApiResponse<Double>> getAverageRating(@PathVariable Long subjectId) {
        Double response = feedbackService.getAverageRating(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Average rating retrieved", response));
    }
}
