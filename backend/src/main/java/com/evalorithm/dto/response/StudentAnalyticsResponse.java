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
public class StudentAnalyticsResponse {

    private Long studentId;
    private String studentName;
    private String subjectName;
    private Integer totalAttempted;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double accuracy;
    private Double averageScore;
    private Double completionRate;
    private Double avgTimePerQuestion;
    private Map<String, Double> difficultyPerformance;
    private List<UnitPerformanceItem> unitPerformance;
    private List<TopicPerformanceItem> topicPerformance;
}
