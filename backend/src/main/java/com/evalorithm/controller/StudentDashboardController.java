package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.StudentDashboardResponse;
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
@RequestMapping("/student/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
@Tag(name = "Student Dashboard", description = "Student dashboard endpoints")
public class StudentDashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get student dashboard data")
    public ResponseEntity<ApiResponse<StudentDashboardResponse>> getStudentDashboard() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        StudentDashboardResponse response = dashboardService.getStudentDashboard(user.getId());
        return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", response));
    }
}
