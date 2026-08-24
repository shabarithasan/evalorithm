package com.evalorithm.controller;

import com.evalorithm.dto.request.AdaptiveExamRequest;
import com.evalorithm.dto.response.AdaptiveExamResponse;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.service.AdaptiveExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/adaptive-exam")
@RequiredArgsConstructor
@Tag(name = "Adaptive Exam", description = "Adaptive exam generation and taking - difficulty adjusts based on performance")
public class AdaptiveExamController {

    private final AdaptiveExamService adaptiveExamService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Create adaptive exam from syllabus", description = "Generates an adaptive exam that starts easy and adjusts difficulty based on student answers")
    public ResponseEntity<ApiResponse<AdaptiveExamResponse>> createAdaptiveExam(@RequestBody AdaptiveExamRequest request) {
        AdaptiveExamResponse response = adaptiveExamService.createAdaptiveExam(request);
        return ResponseEntity.ok(ApiResponse.success("Adaptive exam created", response));
    }

    @GetMapping("/next-question/{attemptId}")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Get next adaptive question", description = "Returns the next question based on previous answer correctness")
    public ResponseEntity<ApiResponse<AdaptiveExamResponse.AdaptiveQuestion>> getNextQuestion(
            @PathVariable Long attemptId,
            @RequestParam boolean previousCorrect,
            @RequestParam(required = false) Long previousQuestionId) {
        AdaptiveExamResponse.AdaptiveQuestion question = adaptiveExamService.getNextAdaptiveQuestion(attemptId, previousCorrect, previousQuestionId);
        return ResponseEntity.ok(ApiResponse.success("Next question", question));
    }
}