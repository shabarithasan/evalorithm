package com.evalorithm.service.impl;

import com.evalorithm.dto.response.AIInsightResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.InsightType;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.AIInsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIInsightServiceImpl implements AIInsightService {

    private final AIInsightRepository aiInsightRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ExamResultRepository examResultRepository;
    private final StudentAnalyticsRepository studentAnalyticsRepository;

    @Override
    @Transactional
    public List<AIInsightResponse> generateInsights(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<AIInsight> existingInsights = aiInsightRepository.findByUserIdOrderByGeneratedAtDesc(userId);
        if (!existingInsights.isEmpty()) {
            return existingInsights.stream().map(this::mapToResponse).collect(Collectors.toList());
        }

        List<AIInsight> insights = new ArrayList<>();
        Optional<StudentProfile> studentProfileOpt = studentProfileRepository.findByUserId(userId);

        if (studentProfileOpt.isPresent()) {
            StudentProfile studentProfile = studentProfileOpt.get();
            List<StudentAnalytics> analyticsList = studentAnalyticsRepository
                    .findByStudentProfileId(studentProfile.getId());

            if (!analyticsList.isEmpty()) {
                StudentAnalytics best = analyticsList.stream()
                        .max(Comparator.comparingDouble(StudentAnalytics::getAccuracy))
                        .orElse(null);
                StudentAnalytics worst = analyticsList.stream()
                        .min(Comparator.comparingDouble(StudentAnalytics::getAccuracy))
                        .orElse(null);

                if (best != null && best.getSubject() != null) {
                    insights.add(AIInsight.builder()
                            .user(user)
                            .insightType(InsightType.BEST_SUBJECT)
                            .title("Best Subject: " + best.getSubject().getName())
                            .description("Your best performing subject is " + best.getSubject().getName()
                                    + " with " + String.format("%.1f", best.getAccuracy()) + "% accuracy.")
                            .subjectName(best.getSubject().getName())
                            .value(best.getAccuracy())
                            .generatedAt(LocalDateTime.now())
                            .build());
                }

                if (worst != null && worst.getSubject() != null) {
                    insights.add(AIInsight.builder()
                            .user(user)
                            .insightType(InsightType.WEAKEST_SUBJECT)
                            .title("Needs Improvement: " + worst.getSubject().getName())
                            .description("Your weakest subject is " + worst.getSubject().getName()
                                    + " with " + String.format("%.1f", worst.getAccuracy()) + "% accuracy. Focus more on this subject.")
                            .subjectName(worst.getSubject().getName())
                            .value(worst.getAccuracy())
                            .generatedAt(LocalDateTime.now())
                            .build());
                }

                double avgAccuracy = analyticsList.stream()
                        .mapToDouble(StudentAnalytics::getAccuracy)
                        .average()
                        .orElse(0.0);

                insights.add(AIInsight.builder()
                        .user(user)
                        .insightType(InsightType.LEARNING_CURVE)
                        .title("Overall Learning Progress")
                        .description("Your average accuracy across all subjects is " + String.format("%.1f", avgAccuracy)
                                + "%. " + (avgAccuracy >= 70 ? "Great progress!" : "Keep working to improve."))
                        .value(avgAccuracy)
                        .generatedAt(LocalDateTime.now())
                        .build());
            }

            List<ExamResult> recentResults = examResultRepository.findByStudentProfileId(studentProfile.getId());
            if (recentResults.size() >= 2) {
                ExamResult earliest = recentResults.get(0);
                ExamResult latest = recentResults.get(recentResults.size() - 1);
                double earlyScore = earliest.getPercentage() != null ? earliest.getPercentage() : 0;
                double latestScore = latest.getPercentage() != null ? latest.getPercentage() : 0;
                double improvement = latestScore - earlyScore;

                insights.add(AIInsight.builder()
                        .user(user)
                        .insightType(InsightType.IMPROVEMENT_TREND)
                        .title("Performance Trend")
                        .description("Your score has " + (improvement >= 0 ? "improved" : "decreased")
                                + " by " + String.format("%.1f", Math.abs(improvement))
                                + "% over recent exams.")
                        .value(improvement)
                        .generatedAt(LocalDateTime.now())
                        .build());
            }
        }

        if (insights.isEmpty()) {
            insights.add(AIInsight.builder()
                    .user(user)
                    .insightType(InsightType.LEARNING_CURVE)
                    .title("Welcome to AI Insights")
                    .description("Start taking exams and adaptive tests to receive personalized insights about your learning.")
                    .value(0.0)
                    .generatedAt(LocalDateTime.now())
                    .build());
        }

        aiInsightRepository.saveAll(insights);

        return aiInsightRepository.findByUserIdOrderByGeneratedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AIInsightResponse> getInsights(Long userId) {
        return aiInsightRepository.findByUserIdOrderByGeneratedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long insightId) {
        AIInsight insight = aiInsightRepository.findById(insightId)
                .orElseThrow(() -> new ResourceNotFoundException("AIInsight", "id", insightId));
        insight.setIsRead(true);
        aiInsightRepository.save(insight);
    }

    private AIInsightResponse mapToResponse(AIInsight insight) {
        return AIInsightResponse.builder()
                .id(insight.getId())
                .insightType(insight.getInsightType() != null ? insight.getInsightType().name() : null)
                .title(insight.getTitle())
                .description(insight.getDescription())
                .subjectName(insight.getSubjectName())
                .value(insight.getValue())
                .generatedAt(insight.getGeneratedAt())
                .build();
    }
}
