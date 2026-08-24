package com.evalorithm.controller;

import com.evalorithm.dto.response.AIInsightResponse;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.service.AIInsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai/insights")
@RequiredArgsConstructor
@Tag(name = "AI Insights", description = "AI-powered learning insights endpoints")
public class AIInsightController {

    private final AIInsightService aiInsightService;

    @PostMapping("/generate/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Generate insights", description = "Generate AI-powered learning insights")
    public ResponseEntity<ApiResponse<List<AIInsightResponse>>> generateInsights(@PathVariable Long userId) {
        List<AIInsightResponse> response = aiInsightService.generateInsights(userId);
        return ResponseEntity.ok(ApiResponse.success("Insights generated", response));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get insights", description = "Get user's AI insights")
    public ResponseEntity<ApiResponse<List<AIInsightResponse>>> getInsights(@PathVariable Long userId) {
        List<AIInsightResponse> response = aiInsightService.getInsights(userId);
        return ResponseEntity.ok(ApiResponse.success("Insights retrieved", response));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Mark as read", description = "Mark insight as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        aiInsightService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read"));
    }
}
