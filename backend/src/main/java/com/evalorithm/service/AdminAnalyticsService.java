package com.evalorithm.service;

import com.evalorithm.dto.response.AdminAnalyticsResponse;

import java.util.List;
import java.util.Map;

public interface AdminAnalyticsService {

    Map<String, Object> getOverallStatistics();

    List<Map<String, Object>> getDepartmentPerformance();

    List<Map<String, Object>> getStudentGrowth();

    List<Map<String, Object>> getTopPerformersGlobal(int limit);

    List<Map<String, Object>> getLowPerformersGlobal(int limit);

    List<Map<String, Object>> getFacultyPerformance();

    AdminAnalyticsResponse getAdminDashboard();
}
