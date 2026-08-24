package com.evalorithm.controller;

import com.evalorithm.dto.request.SystemSettingRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SystemSettingResponse;
import com.evalorithm.service.SystemSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system-settings")
@RequiredArgsConstructor
@Tag(name = "System Settings", description = "System settings management endpoints")
public class SystemSettingController {

    private final SystemSettingService systemSettingService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all settings")
    public ResponseEntity<ApiResponse<PageResponse<SystemSettingResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        PageResponse<SystemSettingResponse> response = systemSettingService.getAllSettings(PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success("Settings retrieved", response));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get settings by category")
    public ResponseEntity<ApiResponse<List<SystemSettingResponse>>> getByCategory(@PathVariable String category) {
        List<SystemSettingResponse> response = systemSettingService.getSettingsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success("Settings retrieved", response));
    }

    @GetMapping("/key/{key}")
    @Operation(summary = "Get setting by key")
    public ResponseEntity<ApiResponse<SystemSettingResponse>> getByKey(@PathVariable String key) {
        SystemSettingResponse response = systemSettingService.getSetting(key);
        return ResponseEntity.ok(ApiResponse.success("Setting retrieved", response));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update setting")
    public ResponseEntity<ApiResponse<SystemSettingResponse>> update(
            @Valid @RequestBody SystemSettingRequest request,
            @RequestParam Long userId) {
        SystemSettingResponse response = systemSettingService.updateSetting(request, userId);
        return ResponseEntity.ok(ApiResponse.success("Setting updated", response));
    }
}
