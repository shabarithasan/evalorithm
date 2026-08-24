package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.LoginHistoryResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.service.LoginHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/login-history")
@RequiredArgsConstructor
@Tag(name = "Login History", description = "Login history and monitoring endpoints")
public class LoginHistoryController {

    private final LoginHistoryService loginHistoryService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get login history for user")
    public ResponseEntity<ApiResponse<PageResponse<LoginHistoryResponse>>> getLoginHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<LoginHistoryResponse> response = loginHistoryService.getLoginHistory(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "loginTime")));
        return ResponseEntity.ok(ApiResponse.success("Login history retrieved", response));
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get active users")
    public ResponseEntity<ApiResponse<List<LoginHistoryResponse>>> getActiveUsers() {
        List<LoginHistoryResponse> response = loginHistoryService.getActiveUsers();
        return ResponseEntity.ok(ApiResponse.success("Active users retrieved", response));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get login statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLoginStats() {
        Map<String, Object> response = loginHistoryService.getLoginStats();
        return ResponseEntity.ok(ApiResponse.success("Login stats retrieved", response));
    }
}
