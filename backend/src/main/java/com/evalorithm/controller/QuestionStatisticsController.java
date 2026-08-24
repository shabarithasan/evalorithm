package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.QuestionStatisticsResponse;
import com.evalorithm.service.QuestionStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question-statistics")
@RequiredArgsConstructor
@Tag(name = "Question Statistics", description = "Question statistics endpoints")
public class QuestionStatisticsController {

    private final QuestionStatisticsService questionStatisticsService;

    @GetMapping("/{questionId}")
    @Operation(summary = "Get question statistics")
    public ResponseEntity<ApiResponse<QuestionStatisticsResponse>> getStatistics(@PathVariable Long questionId) {
        QuestionStatisticsResponse response = questionStatisticsService.getStatistics(questionId);
        return ResponseEntity.ok(ApiResponse.success("Statistics retrieved", response));
    }

    @PostMapping("/{questionId}/view")
    @Operation(summary = "Record question view")
    public ResponseEntity<ApiResponse<Void>> recordView(@PathVariable Long questionId) {
        questionStatisticsService.recordView(questionId);
        return ResponseEntity.ok(ApiResponse.success("View recorded"));
    }

    @PostMapping("/{questionId}/usage")
    @Operation(summary = "Record question usage")
    public ResponseEntity<ApiResponse<Void>> recordUsage(@PathVariable Long questionId,
                                                          @RequestParam boolean correct) {
        questionStatisticsService.recordUsage(questionId, correct);
        return ResponseEntity.ok(ApiResponse.success("Usage recorded"));
    }
}
