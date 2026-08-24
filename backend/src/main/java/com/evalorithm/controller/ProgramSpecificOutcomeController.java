package com.evalorithm.controller;

import com.evalorithm.dto.request.ProgramSpecificOutcomeRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.ProgramSpecificOutcomeResponse;
import com.evalorithm.service.ProgramSpecificOutcomeService;
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
@RequestMapping("/pso")
@RequiredArgsConstructor
@Tag(name = "Program Specific Outcomes", description = "Program Specific Outcome management endpoints")
public class ProgramSpecificOutcomeController {

    private final ProgramSpecificOutcomeService psoService;

    @GetMapping
    @Operation(summary = "Get all program specific outcomes")
    public ResponseEntity<ApiResponse<PageResponse<ProgramSpecificOutcomeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProgramSpecificOutcomeResponse> response = psoService.getAll(PageRequest.of(page, size, Sort.by("code")));
        return ResponseEntity.ok(ApiResponse.success("Program specific outcomes retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get PSO by ID")
    public ResponseEntity<ApiResponse<ProgramSpecificOutcomeResponse>> getById(@PathVariable Long id) {
        ProgramSpecificOutcomeResponse response = psoService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Program specific outcome retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create PSO")
    public ResponseEntity<ApiResponse<ProgramSpecificOutcomeResponse>> create(@Valid @RequestBody ProgramSpecificOutcomeRequest request) {
        ProgramSpecificOutcomeResponse response = psoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Program specific outcome created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update PSO")
    public ResponseEntity<ApiResponse<ProgramSpecificOutcomeResponse>> update(@PathVariable Long id,
                                                                              @Valid @RequestBody ProgramSpecificOutcomeRequest request) {
        ProgramSpecificOutcomeResponse response = psoService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Program specific outcome updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete PSO")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        psoService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Program specific outcome deleted"));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get PSOs by department")
    public ResponseEntity<ApiResponse<List<ProgramSpecificOutcomeResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<ProgramSpecificOutcomeResponse> response = psoService.getByDepartmentId(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Program specific outcomes retrieved", response));
    }
}
