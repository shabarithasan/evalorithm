package com.evalorithm.controller;

import com.evalorithm.dto.request.UnitRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.UnitResponse;
import com.evalorithm.service.UnitService;
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
@RequestMapping("/units")
@RequiredArgsConstructor
@Tag(name = "Units", description = "Unit management endpoints")
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    @Operation(summary = "Get all units")
    public ResponseEntity<ApiResponse<PageResponse<UnitResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        PageResponse<UnitResponse> response = unitService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Units retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get unit by ID")
    public ResponseEntity<ApiResponse<UnitResponse>> getById(@PathVariable Long id) {
        UnitResponse response = unitService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Unit retrieved", response));
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Get units by subject")
    public ResponseEntity<ApiResponse<List<UnitResponse>>> getBySubject(@PathVariable Long subjectId) {
        List<UnitResponse> response = unitService.getBySubject(subjectId);
        return ResponseEntity.ok(ApiResponse.success("Units retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Create unit")
    public ResponseEntity<ApiResponse<UnitResponse>> create(@Valid @RequestBody UnitRequest request) {
        UnitResponse response = unitService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Unit created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Update unit")
    public ResponseEntity<ApiResponse<UnitResponse>> update(@PathVariable Long id,
                                                             @Valid @RequestBody UnitRequest request) {
        UnitResponse response = unitService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Unit updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete unit")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        unitService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Unit deleted"));
    }
}
