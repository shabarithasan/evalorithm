package com.evalorithm.controller;

import com.evalorithm.dto.request.TopicRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.TopicResponse;
import com.evalorithm.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
@Tag(name = "Topics", description = "Topic management endpoints")
public class TopicController {

    private final TopicService topicService;

    @GetMapping
    @Operation(summary = "Get all topics")
    public ResponseEntity<ApiResponse<PageResponse<TopicResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        PageResponse<TopicResponse> response = topicService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Topics retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get topic by ID")
    public ResponseEntity<ApiResponse<TopicResponse>> getById(@PathVariable Long id) {
        TopicResponse response = topicService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Topic retrieved", response));
    }

    @GetMapping("/unit/{unitId}")
    @Operation(summary = "Get topics by unit")
    public ResponseEntity<ApiResponse<List<TopicResponse>>> getByUnit(@PathVariable Long unitId) {
        List<TopicResponse> response = topicService.getByUnit(unitId);
        return ResponseEntity.ok(ApiResponse.success("Topics retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Create topic")
    public ResponseEntity<ApiResponse<TopicResponse>> create(@Valid @RequestBody TopicRequest request) {
        TopicResponse response = topicService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Topic created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Update topic")
    public ResponseEntity<ApiResponse<TopicResponse>> update(@PathVariable Long id,
                                                              @Valid @RequestBody TopicRequest request) {
        TopicResponse response = topicService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Topic updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete topic")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        topicService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Topic deleted"));
    }
}
