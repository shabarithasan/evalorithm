package com.evalorithm.controller;

import com.evalorithm.dto.request.SettingRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.SettingResponse;
import com.evalorithm.service.SettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
@Tag(name = "Settings", description = "System settings endpoints")
public class SettingController {

    private final SettingService settingService;

    @GetMapping
    @Operation(summary = "Get all settings")
    public ResponseEntity<ApiResponse<List<SettingResponse>>> getAllSettings() {
        List<SettingResponse> response = settingService.getAllSettings();
        return ResponseEntity.ok(ApiResponse.success("Settings retrieved", response));
    }

    @GetMapping("/{key}")
    @Operation(summary = "Get setting by key")
    public ResponseEntity<ApiResponse<SettingResponse>> getSetting(@PathVariable String key) {
        SettingResponse response = settingService.getSetting(key);
        return ResponseEntity.ok(ApiResponse.success("Setting retrieved", response));
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update setting (Admin only)")
    public ResponseEntity<ApiResponse<SettingResponse>> updateSetting(@Valid @RequestBody SettingRequest request) {
        SettingResponse response = settingService.updateSetting(request);
        return ResponseEntity.ok(ApiResponse.success("Setting updated", response));
    }
}
