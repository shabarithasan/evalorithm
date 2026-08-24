package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.BackupResponse;
import com.evalorithm.service.BackupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/backups")
@RequiredArgsConstructor
@Tag(name = "Backups", description = "Backup management endpoints")
public class BackupController {

    private final BackupService backupService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create backup")
    public ResponseEntity<ApiResponse<BackupResponse>> create(@RequestParam Long userId) {
        BackupResponse response = backupService.createBackup(userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Backup created", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all backups")
    public ResponseEntity<ApiResponse<List<BackupResponse>>> getAll() {
        List<BackupResponse> response = backupService.getBackups();
        return ResponseEntity.ok(ApiResponse.success("Backups retrieved", response));
    }

    @PostMapping("/{id}/restore")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Restore backup")
    public ResponseEntity<ApiResponse<BackupResponse>> restore(@PathVariable Long id) {
        BackupResponse response = backupService.restoreBackup(id);
        return ResponseEntity.ok(ApiResponse.success("Backup restored", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete backup")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        backupService.deleteBackup(id);
        return ResponseEntity.ok(ApiResponse.success("Backup deleted"));
    }
}
