package com.evalorithm.controller;

import com.evalorithm.dto.request.ReportGenerateRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Report generation endpoints")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Generate report")
    public ResponseEntity<byte[]> generateReport(@Valid @RequestBody ReportGenerateRequest request) {
        byte[] data = reportService.generateReport(request);
        String filename = "report-" + (request.getReportType() != null ? request.getReportType().toLowerCase() : "general") + ".csv";
        return ResponseEntity.ok()
                .header("Content-Type", "text/csv")
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(data);
    }
}
