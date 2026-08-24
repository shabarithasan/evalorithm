package com.evalorithm.service;

import com.evalorithm.dto.response.DashboardResponse;
import com.evalorithm.dto.response.FacultyDashboardResponse;
import com.evalorithm.dto.response.StudentDashboardResponse;

public interface DashboardService {

    DashboardResponse getAdminDashboard();

    FacultyDashboardResponse getFacultyDashboard(Long userId);

    StudentDashboardResponse getStudentDashboard(Long userId);
}
