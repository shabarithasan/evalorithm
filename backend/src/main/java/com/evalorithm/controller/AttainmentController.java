package com.evalorithm.controller;

import com.evalorithm.dto.request.AttainmentRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.AttainmentDashboardResponse;
import com.evalorithm.dto.response.AttainmentResponse;
import com.evalorithm.service.AttainmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attainment")
@RequiredArgsConstructor
@Tag(name = "Attainment", description = "CO attainment calculation and dashboard endpoints")
public class AttainmentController {

    private final AttainmentService attainmentService;

    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Calculate attainment for a CO")
    public ResponseEntity<ApiResponse<AttainmentResponse>> calculateAttainment(@Valid @RequestBody AttainmentRequest request) {
        AttainmentResponse response = attainmentService.calculateAttainment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Attainment calculated", response));
    }

    @GetMapping("/dashboard/{departmentId}/{academicYear}")
    @Operation(summary = "Get attainment dashboard")
    public ResponseEntity<ApiResponse<AttainmentDashboardResponse>> getDashboard(
            @PathVariable Long departmentId, @PathVariable String academicYear) {
        AttainmentDashboardResponse response = attainmentService.getAttainmentDashboard(departmentId, academicYear);
        return ResponseEntity.ok(ApiResponse.success("Attainment dashboard retrieved", response));
    }

    @GetMapping("/subject/{subjectId}/{semesterId}")
    @Operation(summary = "Get attainment by subject and semester")
    public ResponseEntity<ApiResponse<List<AttainmentResponse>>> getBySubject(
            @PathVariable Long subjectId, @PathVariable Long semesterId) {
        List<AttainmentResponse> response = attainmentService.getAttainmentBySubject(subjectId, semesterId);
        return ResponseEntity.ok(ApiResponse.success("Attainment data retrieved", response));
    }

    @GetMapping("/export/{departmentId}/{academicYear}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Export attainment report")
    public ResponseEntity<byte[]> exportReport(
            @PathVariable Long departmentId,
            @PathVariable String academicYear,
            @RequestParam(defaultValue = "CSV") String format) {
        byte[] data = attainmentService.exportAttainmentReport(departmentId, academicYear, format);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=attainment-report.csv")
                .body(data);
    }
}
