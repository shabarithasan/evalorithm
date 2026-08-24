package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAnalyticsResponse {

    private Long totalStudents;
    private Long totalFaculty;
    private Long totalSubjects;
    private Long totalExams;
    private Double overallPassRate;
    private Double averageScore;
    private List<Map<String, Object>> studentGrowth;
    private List<Map<String, Object>> departmentPerformance;
    private List<Map<String, Object>> topPerformers;
    private List<Map<String, Object>> lowPerformers;
}
