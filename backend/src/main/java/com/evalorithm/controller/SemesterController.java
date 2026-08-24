package com.evalorithm.controller;

import com.evalorithm.dto.request.SemesterRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SemesterResponse;
import com.evalorithm.service.SemesterService;
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
@RequestMapping("/semesters")
@RequiredArgsConstructor
@Tag(name = "Semesters", description = "Semester management endpoints")
public class SemesterController {

    private final SemesterService semesterService;

    @GetMapping
    @Operation(summary = "Get all semesters")
    public ResponseEntity<ApiResponse<PageResponse<SemesterResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        PageResponse<SemesterResponse> response = semesterService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Semesters retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get semester by ID")
    public ResponseEntity<ApiResponse<SemesterResponse>> getById(@PathVariable Long id) {
        SemesterResponse response = semesterService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Semester retrieved", response));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get semesters by department")
    public ResponseEntity<ApiResponse<List<SemesterResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<SemesterResponse> response = semesterService.getByDepartment(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Semesters retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create semester")
    public ResponseEntity<ApiResponse<SemesterResponse>> create(@Valid @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Semester created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update semester")
    public ResponseEntity<ApiResponse<SemesterResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody SemesterRequest request) {
        SemesterResponse response = semesterService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Semester updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete semester")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        semesterService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Semester deleted"));
    }
}
