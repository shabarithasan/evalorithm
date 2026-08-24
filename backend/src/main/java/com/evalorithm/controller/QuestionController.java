package com.evalorithm.controller;

import com.evalorithm.dto.request.QuestionApprovalRequest;
import com.evalorithm.dto.request.QuestionRequest;
import com.evalorithm.dto.request.QuestionSearchRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.QuestionDashboardResponse;
import com.evalorithm.dto.response.QuestionResponse;
import com.evalorithm.dto.response.QuestionVersionResponse;
import com.evalorithm.entity.User;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.service.QuestionService;
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
@RequestMapping("/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "Question repository management endpoints")
public class QuestionController {

    private final QuestionService questionService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get all questions", description = "Get paginated and filtered list of questions")
    public ResponseEntity<ApiResponse<PageResponse<QuestionResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String questionType,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String bloomLevel,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long createdBy,
            @RequestParam(required = false) String searchTerm) {

        QuestionSearchRequest searchRequest = new QuestionSearchRequest();
        searchRequest.setDepartmentId(departmentId);
        searchRequest.setSemesterId(semesterId);
        searchRequest.setSubjectId(subjectId);
        searchRequest.setUnitId(unitId);
        searchRequest.setTopicId(topicId);
        searchRequest.setCategoryId(categoryId);
        searchRequest.setCreatedBy(createdBy);
        searchRequest.setSearchTerm(searchTerm);

        if (questionType != null) {
            searchRequest.setQuestionType(com.evalorithm.enums.QuestionType.valueOf(questionType));
        }
        if (difficulty != null) {
            searchRequest.setDifficulty(com.evalorithm.enums.QuestionDifficulty.valueOf(difficulty));
        }
        if (bloomLevel != null) {
            searchRequest.setBloomLevel(com.evalorithm.enums.BloomLevel.valueOf(bloomLevel));
        }
        if (status != null) {
            searchRequest.setStatus(com.evalorithm.enums.QuestionStatus.valueOf(status));
        }

        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        PageResponse<QuestionResponse> response = questionService.getAllQuestions(searchRequest, pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Questions retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question by ID")
    public ResponseEntity<ApiResponse<QuestionResponse>> getById(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success("Question retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Create question", description = "Create a new question (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionResponse>> create(@Valid @RequestBody QuestionRequest request) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.createQuestion(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Update question", description = "Update an existing question (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody QuestionRequest request) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.updateQuestion(id, request, userId);
        return ResponseEntity.ok(ApiResponse.success("Question updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete question", description = "Delete a question (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Question deleted"));
    }

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Duplicate question", description = "Duplicate an existing question (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionResponse>> duplicate(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        QuestionResponse response = questionService.duplicateQuestion(id, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question duplicated", response));
    }

    @PutMapping("/{id}/archive")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Archive question", description = "Archive a question (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionResponse>> archive(@PathVariable Long id) {
        QuestionResponse response = questionService.archiveQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Question archived", response));
    }

    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore question", description = "Restore an archived question (Admin only)")
    public ResponseEntity<ApiResponse<QuestionResponse>> restore(@PathVariable Long id) {
        QuestionResponse response = questionService.restoreQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Question restored", response));
    }

    @PutMapping("/{id}/submit-review")
    @PreAuthorize("hasRole('FACULTY')")
    @Operation(summary = "Submit for review", description = "Submit question for review (Faculty only)")
    public ResponseEntity<ApiResponse<QuestionResponse>> submitForReview(@PathVariable Long id) {
        QuestionResponse response = questionService.submitForReview(id);
        return ResponseEntity.ok(ApiResponse.success("Question submitted for review", response));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve/Reject question", description = "Approve or reject a question (Admin only)")
    public ResponseEntity<ApiResponse<QuestionResponse>> approve(@PathVariable Long id,
                                                                  @Valid @RequestBody QuestionApprovalRequest request) {
        Long approverId = getCurrentUserId();
        QuestionResponse response = questionService.approveQuestion(id, approverId, request);
        return ResponseEntity.ok(ApiResponse.success("Question review updated", response));
    }

    @GetMapping("/{id}/versions")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get question versions", description = "Get version history of a question (Admin/Faculty)")
    public ResponseEntity<ApiResponse<List<QuestionVersionResponse>>> getVersions(@PathVariable Long id) {
        List<QuestionVersionResponse> response = questionService.getQuestionVersions(id);
        return ResponseEntity.ok(ApiResponse.success("Question versions retrieved", response));
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Get question dashboard", description = "Get question statistics dashboard (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionDashboardResponse>> getDashboard() {
        QuestionDashboardResponse response = questionService.getQuestionDashboard();
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
