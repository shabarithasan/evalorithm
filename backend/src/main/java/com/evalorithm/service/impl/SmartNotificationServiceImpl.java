package com.evalorithm.service.impl;

import com.evalorithm.dto.response.SmartNotificationResponse;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.SmartNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SmartNotificationServiceImpl implements SmartNotificationService {

    private final StudentProfileRepository studentProfileRepository;
    private final ExamResultRepository examResultRepository;
    private final AdaptiveSessionRepository adaptiveSessionRepository;
    private final LearningHistoryRepository learningHistoryRepository;
    private final ExamRepository examRepository;

    @Override
    public List<SmartNotificationResponse> generateSmartNotifications(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        List<SmartNotificationResponse> notifications = new ArrayList<>();

        List<ExamResult> recentResults = examResultRepository.findByStudentProfileId(studentProfile.getId());
        if (!recentResults.isEmpty()) {
            ExamResult latest = recentResults.get(recentResults.size() - 1);
            if (latest.getPercentage() != null && latest.getPercentage() < 50) {
                notifications.add(SmartNotificationResponse.builder()
                        .title("Low Performance Alert")
                        .message("Your recent score was " + String.format("%.1f", latest.getPercentage())
                                + "%. Focus on improving your weaker areas.")
                        .type("LOW_PERFORMANCE")
                        .priority("HIGH")
                        .generatedAt(LocalDateTime.now())
                        .build());
            }
        }

        Optional<AdaptiveSession> activeSession = adaptiveSessionRepository
                .findByStudentProfileIdAndIsActiveTrue(studentProfile.getId());
        if (activeSession.isPresent()) {
            AdaptiveSession session = activeSession.get();
            double accuracy = session.getQuestionsAnswered() > 0
                    ? (double) session.getCorrectAnswers() / session.getQuestionsAnswered() * 100
                    : 0;
            if (accuracy >= 80 && session.getQuestionsAnswered() >= 10) {
                notifications.add(SmartNotificationResponse.builder()
                        .title("Exam Readiness")
                        .message("Your adaptive test performance is strong (" + String.format("%.0f", accuracy)
                                + "%). You're ready to take the actual exam!")
                        .type("EXAM_READY")
                        .priority("MEDIUM")
                        .generatedAt(LocalDateTime.now())
                        .build());
            }
        }

        List<LearningHistory> recentActivity = learningHistoryRepository
                .findByStudentProfileIdAndRecordedAtAfter(studentProfile.getId(), LocalDateTime.now().minusDays(3));
        if (recentActivity.isEmpty()) {
            notifications.add(SmartNotificationResponse.builder()
                    .title("Practice Reminder")
                    .message("You haven't practiced in 3+ days. Regular practice helps maintain and improve your skills.")
                    .type("PRACTICE_REMINDER")
                    .priority("MEDIUM")
                    .generatedAt(LocalDateTime.now())
                    .build());
        }

        List<Exam> upcomingExams = examRepository.findAll().stream()
                .filter(e -> e.getStartDate() != null && e.getStartDate().isAfter(LocalDateTime.now())
                        && e.getStartDate().isBefore(LocalDateTime.now().plusDays(3)))
                .collect(Collectors.toList());

        for (Exam exam : upcomingExams) {
            notifications.add(SmartNotificationResponse.builder()
                    .title("Upcoming Exam: " + exam.getTitle())
                    .message("You have an exam scheduled in less than 3 days. Start preparing now!")
                    .type("UPCOMING_EXAM")
                    .priority("HIGH")
                    .generatedAt(LocalDateTime.now())
                    .build());
        }

        if (notifications.isEmpty()) {
            notifications.add(SmartNotificationResponse.builder()
                    .title("All Good!")
                    .message("Keep up the good work! Continue your learning streak.")
                    .type("GENERAL")
                    .priority("LOW")
                    .generatedAt(LocalDateTime.now())
                    .build());
        }

        return notifications;
    }

    @Override
    public List<SmartNotificationResponse> getSmartNotifications(Long studentId) {
        return generateSmartNotifications(studentId);
    }
}
