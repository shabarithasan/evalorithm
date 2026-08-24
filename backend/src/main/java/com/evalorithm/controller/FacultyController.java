package com.evalorithm.controller;

import com.evalorithm.dto.request.FacultyRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.FacultyResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.service.FacultyService;
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
@RequestMapping("/faculty")
@RequiredArgsConstructor
@Tag(name = "Faculty", description = "Faculty management endpoints")
public class FacultyController {

    private final FacultyService facultyService;

    @GetMapping
    @Operation(summary = "Get all faculty")
    public ResponseEntity<ApiResponse<PageResponse<FacultyResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        PageResponse<FacultyResponse> response = facultyService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Faculty retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get faculty by ID")
    public ResponseEntity<ApiResponse<FacultyResponse>> getById(@PathVariable Long id) {
        FacultyResponse response = facultyService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Faculty retrieved", response));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get faculty by department")
    public ResponseEntity<ApiResponse<List<FacultyResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<FacultyResponse> response = facultyService.getByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Faculty retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create faculty")
    public ResponseEntity<ApiResponse<FacultyResponse>> create(@Valid @RequestBody FacultyRequest request) {
        FacultyResponse response = facultyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Faculty created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update faculty")
    public ResponseEntity<ApiResponse<FacultyResponse>> update(@PathVariable Long id,
                                                                @Valid @RequestBody FacultyRequest request) {
        FacultyResponse response = facultyService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Faculty updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete faculty")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        facultyService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Faculty deleted"));
    }

    @PostMapping("/{id}/assign-subjects")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign subjects to faculty")
    public ResponseEntity<ApiResponse<FacultyResponse>> assignSubjects(@PathVariable Long id,
                                                                        @RequestBody List<Long> subjectIds) {
        FacultyResponse response = facultyService.assignSubjects(id, subjectIds);
        return ResponseEntity.ok(ApiResponse.success("Subjects assigned", response));
    }

    @PostMapping("/{id}/remove-subjects")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove subjects from faculty")
    public ResponseEntity<ApiResponse<FacultyResponse>> removeSubjects(@PathVariable Long id,
                                                                        @RequestBody List<Long> subjectIds) {
        FacultyResponse response = facultyService.removeSubjects(id, subjectIds);
        return ResponseEntity.ok(ApiResponse.success("Subjects removed", response));
    }
}
