package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PredictionResponse;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/predictions")
@RequiredArgsConstructor
@Tag(name = "Predictions", description = "Performance prediction endpoints")
public class PredictionController {

    private final PredictionService predictionService;
    private final UserRepository userRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Predict performance", description = "Generate performance prediction for a subject")
    public ResponseEntity<ApiResponse<PredictionResponse>> predictPerformance(
            @RequestParam Long subjectId) {
        Long userId = getCurrentUserId();
        PredictionResponse response = predictionService.predictPerformance(userId, subjectId);
        return ResponseEntity.ok(ApiResponse.success("Prediction generated", response));
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get predictions", description = "Get all predictions for a student")
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> getPredictions(@PathVariable Long studentId) {
        List<PredictionResponse> response = predictionService.getPredictions(studentId);
        return ResponseEntity.ok(ApiResponse.success("Predictions retrieved", response));
    }

    @GetMapping("/risk-students/{subjectId}")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    @Operation(summary = "Get risk students", description = "Get students at risk of failing a subject")
    public ResponseEntity<ApiResponse<List<PredictionResponse>>> getRiskStudents(
            @PathVariable Long subjectId) {
        List<PredictionResponse> response = predictionService.getRiskStudents(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Risk students retrieved", response));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
