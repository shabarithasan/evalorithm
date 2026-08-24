package com.evalorithm.controller;

import com.evalorithm.dto.request.ProgramOutcomeRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.ProgramOutcomeResponse;
import com.evalorithm.service.ProgramOutcomeService;
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
@RequestMapping("/po")
@RequiredArgsConstructor
@Tag(name = "Program Outcomes", description = "Program Outcome management endpoints")
public class ProgramOutcomeController {

    private final ProgramOutcomeService programOutcomeService;

    @GetMapping
    @Operation(summary = "Get all program outcomes")
    public ResponseEntity<ApiResponse<PageResponse<ProgramOutcomeResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<ProgramOutcomeResponse> response = programOutcomeService.getAll(PageRequest.of(page, size, Sort.by("code")));
        return ResponseEntity.ok(ApiResponse.success("Program outcomes retrieved", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get program outcome by ID")
    public ResponseEntity<ApiResponse<ProgramOutcomeResponse>> getById(@PathVariable Long id) {
        ProgramOutcomeResponse response = programOutcomeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Program outcome retrieved", response));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create program outcome")
    public ResponseEntity<ApiResponse<ProgramOutcomeResponse>> create(@Valid @RequestBody ProgramOutcomeRequest request) {
        ProgramOutcomeResponse response = programOutcomeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Program outcome created", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update program outcome")
    public ResponseEntity<ApiResponse<ProgramOutcomeResponse>> update(@PathVariable Long id,
                                                                      @Valid @RequestBody ProgramOutcomeRequest request) {
        ProgramOutcomeResponse response = programOutcomeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Program outcome updated", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete program outcome")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        programOutcomeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Program outcome deleted"));
    }

    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get POs by department")
    public ResponseEntity<ApiResponse<List<ProgramOutcomeResponse>>> getByDepartment(@PathVariable Long departmentId) {
        List<ProgramOutcomeResponse> response = programOutcomeService.getByDepartmentId(departmentId);
        return ResponseEntity.ok(ApiResponse.success("Program outcomes retrieved", response));
    }
}
