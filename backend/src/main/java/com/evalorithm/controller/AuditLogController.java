package com.evalorithm.controller;

import com.evalorithm.dto.request.AuditLogFilterRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.AuditLogResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit Logs", description = "Audit log management endpoints")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit logs with filters")
    public ResponseEntity<ApiResponse<PageResponse<AuditLogResponse>>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        AuditLogFilterRequest filter = new AuditLogFilterRequest(userId, action, entityName, startDate, endDate);
        PageResponse<AuditLogResponse> response = auditLogService.getAuditLogs(filter, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp")));
        return ResponseEntity.ok(ApiResponse.success("Audit logs retrieved", response));
    }

    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get recent activity")
    public ResponseEntity<ApiResponse<List<AuditLogResponse>>> getRecentActivity(
            @RequestParam(defaultValue = "20") int limit) {
        List<AuditLogResponse> response = auditLogService.getRecentActivity(limit);
        return ResponseEntity.ok(ApiResponse.success("Recent activity retrieved", response));
    }
}
