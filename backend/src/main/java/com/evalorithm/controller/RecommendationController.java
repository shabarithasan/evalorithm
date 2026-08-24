package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.RecommendationResponse;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.RecommendationEngineService;
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
@RequestMapping("/recommendations")
@RequiredArgsConstructor
@Tag(name = "Recommendations", description = "Learning recommendation endpoints")
public class RecommendationController {

    private final RecommendationEngineService recommendationEngineService;
    private final UserRepository userRepository;

    @PostMapping("/generate/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
    @Operation(summary = "Generate recommendations", description = "Generate personalized learning recommendations")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> generateRecommendations(
            @PathVariable Long studentId) {
        Long userId = getCurrentUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Long targetStudentId = userId;
        if (currentUser.getRole() != null && currentUser.getRole().name().contains("ADMIN")) {
            targetStudentId = studentId;
        }

        List<RecommendationResponse> response = recommendationEngineService.generateRecommendations(targetStudentId);
        return ResponseEntity.ok(ApiResponse.success("Recommendations generated", response));
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'FACULTY', 'ADMIN')")
    @Operation(summary = "Get recommendations", description = "Get student's learning recommendations")
    public ResponseEntity<ApiResponse<List<RecommendationResponse>>> getRecommendations(
            @PathVariable Long studentId) {
        List<RecommendationResponse> response = recommendationEngineService.getRecommendations(studentId);
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved", response));
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Mark as read", description = "Mark recommendation as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        recommendationEngineService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Marked as read"));
    }

    @PutMapping("/{id}/accept")
    @PreAuthorize("hasRole('STUDENT')")
    @Operation(summary = "Accept recommendation", description = "Accept a learning recommendation")
    public ResponseEntity<ApiResponse<Void>> acceptRecommendation(@PathVariable Long id) {
        recommendationEngineService.acceptRecommendation(id);
        return ResponseEntity.ok(ApiResponse.success("Recommendation accepted"));
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return user.getId();
    }
}
