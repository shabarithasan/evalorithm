package com.evalorithm.service;

import com.evalorithm.dto.response.*;

import java.util.List;
import java.util.Map;

public interface StudentAnalyticsService {

    StudentAnalyticsResponse calculateStudentAnalytics(Long studentId, Long subjectId);

    Map<String, Object> getStudentDashboard(Long studentId);

    List<SubjectPerformanceItem> getSubjectPerformance(Long studentId);

    List<UnitPerformanceItem> getUnitPerformance(Long studentId, Long subjectId);

    List<TopicPerformanceItem> getTopicPerformance(Long studentId, Long subjectId);

    Map<String, Double> getDifficultyPerformance(Long studentId);

    List<Map<String, Object>> getAccuracyOverTime(Long studentId);
}
