package com.evalorithm.controller;

import com.evalorithm.dto.request.COMappingRequest;
import com.evalorithm.dto.request.CourseOutcomeRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.COMappingResponse;
import com.evalorithm.dto.response.CourseOutcomeResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.service.COMappingService;
import com.evalorithm.service.CourseOutcomeService;
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
@RequestMapping("/co")
@RequiredArgsConstructor
@Tag(name = "Course Outcomes", description = "Course Outcome, CO-PO-PSO mapping endpoints")
public class CourseOutcomeController {

    private final CourseOutcomeService courseOutcomeService;
    private final COMappingService coMappingService;

    @GetMapping
    @Operation(summary = "Get all course outcomes")
    public ResponseEntity<ApiResponse<PageResponse<CourseOutcomeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<CourseOutcomeResponse> response = courseOutcomeService.getAll(PageRequest.of(page, size, Sort.by("code")));
        return ResponseEntity.ok(ApiResponse.success("Course outcomes retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get course outcome by ID")
    public ResponseEntity<ApiResponse<CourseOutcomeResponse>> getById(@PathVariable Long id) {
        CourseOutcomeResponse response = courseOutcomeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Course outcome retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Create course outcome")
    public ResponseEntity<ApiResponse<CourseOutcomeResponse>> create(@Valid @RequestBody CourseOutcomeRequest request) {
        CourseOutcomeResponse response = courseOutcomeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course outcome created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Update course outcome")
    public ResponseEntity<ApiResponse<CourseOutcomeResponse>> update(@PathVariable Long id,
                                                                     @Valid @RequestBody CourseOutcomeRequest request) {
        CourseOutcomeResponse response = courseOutcomeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Course outcome updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete course outcome")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        courseOutcomeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Course outcome deleted"));
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get COs by subject")
    public ResponseEntity<ApiResponse<List<CourseOutcomeResponse>>> getBySubject(@PathVariable Long subjectId) {
        List<CourseOutcomeResponse> response = courseOutcomeService.getBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Course outcomes retrieved", response));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get COs by department")
    public ResponseEntity<ApiResponse<List<CourseOutcomeResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<CourseOutcomeResponse> response = courseOutcomeService.getByDepartmentId(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Course outcomes retrieved", response));
    }

    @PostMapping("/map-question")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Map question to CO")
    public ResponseEntity<ApiResponse<COMappingResponse>> mapQuestionToCO(@Valid @RequestBody COMappingRequest request) {
        COMappingResponse response = coMappingService.mapQuestionToCO(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Question mapped to CO", response));
    }

    @DeleteMapping("/mapping/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Remove CO mapping")
    public ResponseEntity<ApiResponse<Void>> removeMapping(@PathVariable Long id) {
        coMappingService.removeMapping(id);
        return ResponseEntity.ok(ApiResponse.success("Mapping removed"));
    }

    @GetMapping("/mapped-questions/{coId}")
    @Operation(summary = "Get mapped questions for CO")
    public ResponseEntity<ApiResponse<List<COMappingResponse>>> getMappedQuestions(@PathVariable Long coId) {
        List<COMappingResponse> response = coMappingService.getMappedQuestions(coId);
        return ResponseEntity.ok(ApiResponse.success("Mapped questions retrieved", response));
    }
}
