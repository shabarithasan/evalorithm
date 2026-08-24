package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.FacultyDashboardResponse;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/faculty/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FACULTY')")
@Tag(name = "Faculty Dashboard", description = "Faculty dashboard endpoints")
public class FacultyDashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get faculty dashboard data")
    public ResponseEntity<ApiResponse<FacultyDashboardResponse>> getFacultyDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        FacultyDashboardResponse response = dashboardService.getFacultyDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", response));
    }
}
