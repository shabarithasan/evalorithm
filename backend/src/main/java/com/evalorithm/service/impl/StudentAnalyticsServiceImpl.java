package com.evalorithm.service.impl;

import com.evalorithm.dto.response.*;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.StudentAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentAnalyticsServiceImpl implements StudentAnalyticsService {

    private final StudentAnalyticsRepository studentAnalyticsRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;
    private final ExamResultRepository examResultRepository;
    private final AdaptiveSessionRepository adaptiveSessionRepository;
    private final LearningHistoryRepository learningHistoryRepository;
    private final UnitRepository unitRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudentAnalyticsResponse calculateStudentAnalytics(Long studentId, Long subjectId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));

        List<ExamResult> examResults = examResultRepository.findByStudentProfileId(studentProfile.getId());
        List<com.evalorithm.entity.AdaptiveSession> adaptiveSessions = adaptiveSessionRepository
                .findByStudentProfileId(studentProfile.getId());
        List<LearningHistory> learningHistoryList = learningHistoryRepository
                .findByStudentProfileIdAndSubjectId(studentProfile.getId(), subjectId);

        int totalAttempted = examResults.stream()
                .mapToInt(er -> er.getCorrectAnswers() != null ? er.getCorrectAnswers() : 0)
                .sum()
                + adaptiveSessions.stream()
                .mapToInt(AdaptiveSession::getCorrectAnswers)
                .sum();

        int correctAnswers = examResults.stream()
                .mapToInt(er -> er.getCorrectAnswers() != null ? er.getCorrectAnswers() : 0)
                .sum()
                + adaptiveSessions.stream()
                .mapToInt(AdaptiveSession::getCorrectAnswers)
                .sum();

        int wrongAnswers = examResults.stream()
                .mapToInt(er -> er.getWrongAnswers() != null ? er.getWrongAnswers() : 0)
                .sum()
                + adaptiveSessions.stream()
                .mapToInt(AdaptiveSession::getWrongAnswers)
                .sum();

        double accuracy = totalAttempted > 0 ? (double) correctAnswers / totalAttempted * 100 : 0.0;

        double averageScore = examResults.stream()
                .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                .average()
                .orElse(0.0);

        double completionRate = learningHistoryList.isEmpty() ? 0.0 :
                (double) learningHistoryList.size() / Math.max(learningHistoryList.size(), 1) * 100;

        double avgTime = learningHistoryList.stream()
                .filter(lh -> lh.getTimeSpentMinutes() != null)
                .mapToInt(LearningHistory::getTimeSpentMinutes)
                .average()
                .orElse(0.0);

        StudentAnalytics analytics = studentAnalyticsRepository
                .findByStudentProfileIdAndSubjectId(studentProfile.getId(), subjectId)
                .orElse(StudentAnalytics.builder()
                        .studentProfile(studentProfile)
                        .subject(subject)
                        .build());

        analytics.setTotalQuestionsAttempted(totalAttempted);
        analytics.setCorrectAnswers(correctAnswers);
        analytics.setWrongAnswers(wrongAnswers);
        analytics.setAverageScore(Math.round(averageScore * 100.0) / 100.0);
        analytics.setAccuracy(Math.round(accuracy * 100.0) / 100.0);
        analytics.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);
        analytics.setAverageTimePerQuestion(avgTime);
        analytics.setLastCalculatedAt(LocalDateTime.now());

        studentAnalyticsRepository.save(analytics);

        Map<String, Double> difficultyPerformance = new HashMap<>();
        adaptiveSessions.forEach(s -> {
            if (s.getCurrentDifficulty() != null) {
                difficultyPerformance.merge(s.getCurrentDifficulty().name(),
                        s.getQuestionsAnswered() > 0 ? (double) s.getCorrectAnswers() / s.getQuestionsAnswered() * 100 : 0.0,
                        (a, b) -> (a + b) / 2);
            }
        });

        String studentName = "";
        if (studentProfile.getUser() != null) {
            studentName = (studentProfile.getUser().getFirstName() != null ? studentProfile.getUser().getFirstName() : "") +
                    (studentProfile.getUser().getLastName() != null ? " " + studentProfile.getUser().getLastName() : "");
        }

        return StudentAnalyticsResponse.builder()
                .studentId(studentId)
                .studentName(studentName.trim())
                .subjectName(subject.getName())
                .totalAttempted(totalAttempted)
                .correctAnswers(correctAnswers)
                .wrongAnswers(wrongAnswers)
                .accuracy(Math.round(accuracy * 100.0) / 100.0)
                .averageScore(Math.round(averageScore * 100.0) / 100.0)
                .completionRate(Math.round(completionRate * 100.0) / 100.0)
                .avgTimePerQuestion(avgTime)
                .difficultyPerformance(difficultyPerformance)
                .unitPerformance(new ArrayList<>())
                .topicPerformance(new ArrayList<>())
                .build();
    }

    @Override
    public Map<String, Object> getStudentDashboard(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        List<StudentAnalytics> allAnalytics = studentAnalyticsRepository
                .findByStudentProfileId(studentProfile.getId());

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("totalSubjects", allAnalytics.size());
        dashboard.put("overallAccuracy", allAnalytics.stream()
                .mapToDouble(StudentAnalytics::getAccuracy)
                .average()
                .orElse(0.0));
        dashboard.put("totalQuestionsAttempted", allAnalytics.stream()
                .mapToInt(StudentAnalytics::getTotalQuestionsAttempted)
                .sum());
        dashboard.put("averageScore", allAnalytics.stream()
                .mapToDouble(StudentAnalytics::getAverageScore)
                .average()
                .orElse(0.0));

        long weakSubjects = allAnalytics.stream()
                .filter(a -> a.getAccuracy() < 50)
                .count();
        long strongSubjects = allAnalytics.stream()
                .filter(a -> a.getAccuracy() >= 75)
                .count();
        dashboard.put("weakSubjects", weakSubjects);
        dashboard.put("strongSubjects", strongSubjects);

        return dashboard;
    }

    @Override
    public List<SubjectPerformanceItem> getSubjectPerformance(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        return studentAnalyticsRepository.findByStudentProfileId(studentProfile.getId())
                .stream()
                .map(sa -> SubjectPerformanceItem.builder()
                        .subjectName(sa.getSubject() != null ? sa.getSubject().getName() : "Unknown")
                        .accuracy(sa.getAccuracy())
                        .totalQuestions(sa.getTotalQuestionsAttempted())
                        .averageTime(sa.getAverageTimePerQuestion())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<UnitPerformanceItem> getUnitPerformance(Long studentId, Long subjectId) {
        return new ArrayList<>();
    }

    @Override
    public List<TopicPerformanceItem> getTopicPerformance(Long studentId, Long subjectId) {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Double> getDifficultyPerformance(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        List<com.evalorithm.entity.AdaptiveSession> sessions = adaptiveSessionRepository
                .findByStudentProfileId(studentProfile.getId());

        Map<String, Double> performance = new HashMap<>();
        sessions.forEach(s -> {
            if (s.getCurrentDifficulty() != null && s.getQuestionsAnswered() > 0) {
                double acc = (double) s.getCorrectAnswers() / s.getQuestionsAnswered() * 100;
                performance.merge(s.getCurrentDifficulty().name(), acc, (a, b) -> (a + b) / 2);
            }
        });
        return performance;
    }

    @Override
    public List<Map<String, Object>> getAccuracyOverTime(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        List<LearningHistory> history = learningHistoryRepository
                .findByStudentProfileIdOrderByRecordedAtDesc(studentProfile.getId());

        return history.stream()
                .filter(lh -> lh.getScore() != null)
                .collect(Collectors.groupingBy(
                        lh -> lh.getRecordedAt() != null ? lh.getRecordedAt().toLocalDate().toString() : "unknown",
                        LinkedHashMap::new,
                        Collectors.toList()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Map<String, Object> point = new HashMap<>();
                    point.put("date", entry.getKey());
                    point.put("accuracy", entry.getValue().stream()
                            .mapToDouble(lh -> lh.getScore() != null ? lh.getScore() : 0.0)
                            .average()
                            .orElse(0.0));
                    return point;
                })
                .collect(Collectors.toList());
    }
}
