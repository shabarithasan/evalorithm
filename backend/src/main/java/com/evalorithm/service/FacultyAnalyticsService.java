package com.evalorithm.service;

import com.evalorithm.dto.response.FacultyAnalyticsResponse;

import java.util.List;
import java.util.Map;

public interface FacultyAnalyticsService {

    FacultyAnalyticsResponse calculateFacultyAnalytics(Long facultyId);

    Map<String, Object> getClassPerformance(Long facultyId, Long subjectId);

    List<Map<String, Object>> getTopPerformers(Long facultyId, Long subjectId, int limit);

    List<Map<String, Object>> getLowPerformers(Long facultyId, Long subjectId, int limit);

    List<FacultyAnalyticsResponse> getSubjectAnalysis(Long facultyId);

    Map<String, Object> getQuestionDifficultyAnalysis(Long facultyId);
}
