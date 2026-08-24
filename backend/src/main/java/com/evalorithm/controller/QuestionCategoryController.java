package com.evalorithm.controller;

import com.evalorithm.dto.request.QuestionCategoryRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.QuestionCategoryResponse;
import com.evalorithm.service.QuestionCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/question-categories")
@RequiredArgsConstructor
@Tag(name = "Question Categories", description = "Question category management endpoints")
public class QuestionCategoryController {

    private final QuestionCategoryService questionCategoryService;

    @GetMapping
    @Operation(summary = "Get all question categories", description = "Get paginated list of question categories")
    public ResponseEntity<ApiResponse<PageResponse<QuestionCategoryResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        org.springframework.data.domain.PageRequest pageRequest = com.evalorithm.util.PaginationUtil.createPageRequest(page, size, sortBy, sortDir);
        PageResponse<QuestionCategoryResponse> response = questionCategoryService.getAll(pageRequest, search);
        return ResponseEntity.ok(ApiResponse.success("Question categories retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get question category by ID")
    public ResponseEntity<ApiResponse<QuestionCategoryResponse>> getById(@PathVariable Long id) {
        QuestionCategoryResponse response = questionCategoryService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Question category retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Create question category", description = "Create a new question category (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionCategoryResponse>> create(@Valid @RequestBody QuestionCategoryRequest request) {
        QuestionCategoryResponse response = questionCategoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question category created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Update question category", description = "Update an existing question category (Admin/Faculty)")
    public ResponseEntity<ApiResponse<QuestionCategoryResponse>> update(@PathVariable Long id,
                                                                         @Valid @RequestBody QuestionCategoryRequest request) {
        QuestionCategoryResponse response = questionCategoryService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Question category updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete question category", description = "Delete a question category (Admin only)")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        questionCategoryService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Question category deleted"));
    }
}
