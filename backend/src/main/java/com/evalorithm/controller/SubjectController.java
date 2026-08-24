package com.evalorithm.controller;

import com.evalorithm.dto.request.SubjectRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SubjectResponse;
import com.evalorithm.service.SubjectService;
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
@RequestMapping("/subjects")
@RequiredArgsConstructor
@Tag(name = "Subjects", description = "Subject management endpoints")
public class SubjectController {

    private final SubjectService subjectService;

    @GetMapping
    @Operation(summary = "Get all subjects")
    public ResponseEntity<ApiResponse<PageResponse<SubjectResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        PageResponse<SubjectResponse> response = subjectService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID")
    public ResponseEntity<ApiResponse<SubjectResponse>> getById(@PathVariable Long id) {
        SubjectResponse response = subjectService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Subject retrieved", response));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get subjects by department")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<SubjectResponse> response = subjectService.getByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved", response));
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get subjects by semester")
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getBySemester(@PathVariable Long semesterId) {
        List<SubjectResponse> response = subjectService.getBySemester(semesterId);
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Create subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> create(@Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subject created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Update subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subject updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete subject")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted"));
    }
}
