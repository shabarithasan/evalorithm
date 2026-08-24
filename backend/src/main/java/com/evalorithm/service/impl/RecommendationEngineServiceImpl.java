package com.evalorithm.service.impl;

import com.evalorithm.dto.response.RecommendationResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.LearningPriority;
import com.evalorithm.enums.RecommendationType;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.RecommendationEngineService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationEngineServiceImpl implements RecommendationEngineService {

    private final RecommendationRepository recommendationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;
    private final ExamResultRepository examResultRepository;
    private final AdaptiveSessionRepository adaptiveSessionRepository;
    private final LearningHistoryRepository learningHistoryRepository;

    @Override
    @Transactional
    public List<RecommendationResponse> generateRecommendations(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        List<Recommendation> existingRecs = recommendationRepository
                .findByStudentProfileIdOrderByGeneratedAtDesc(studentProfile.getId());
        if (!existingRecs.isEmpty()) {
            return existingRecs.stream().map(this::mapToResponse).collect(Collectors.toList());
        }

        List<Subject> enrolledSubjects = studentProfile.getEnrolledSubjects();
        if (enrolledSubjects == null) enrolledSubjects = new ArrayList<>();

        List<Recommendation> recommendations = new ArrayList<>();

        for (Subject subject : enrolledSubjects) {
            List<ExamResult> results = examResultRepository.findByStudentProfileId(studentProfile.getId());
            double accuracy = results.stream()
                    .filter(er -> er.getExam() != null && er.getExam().getSubject() != null
                            && er.getExam().getSubject().getId().equals(subject.getId()))
                    .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                    .average()
                    .orElse(0.0);

            if (accuracy > 0 && accuracy < 40) {
                recommendations.add(Recommendation.builder()
                        .studentProfile(studentProfile)
                        .recommendationType(RecommendationType.REVISION)
                        .title("Critical: Revision needed for " + subject.getName())
                        .description("Your accuracy in " + subject.getName() + " is " + String.format("%.1f", accuracy)
                                + "%. Immediate revision is strongly recommended. Focus on fundamental concepts.")
                        .priority(LearningPriority.CRITICAL)
                        .relatedSubject(subject)
                        .generatedAt(LocalDateTime.now())
                        .build());
            } else if (accuracy >= 40 && accuracy < 60) {
                recommendations.add(Recommendation.builder()
                        .studentProfile(studentProfile)
                        .recommendationType(RecommendationType.PRACTICE_QUESTIONS)
                        .title("Practice recommended for " + subject.getName())
                        .description("Your accuracy in " + subject.getName() + " is " + String.format("%.1f", accuracy)
                                + "%. Solve more practice questions to improve.")
                        .priority(LearningPriority.HIGH)
                        .relatedSubject(subject)
                        .generatedAt(LocalDateTime.now())
                        .build());
            } else if (accuracy >= 60 && accuracy < 75) {
                recommendations.add(Recommendation.builder()
                        .studentProfile(studentProfile)
                        .recommendationType(RecommendationType.MOCK_TEST)
                        .title("Take a mock test for " + subject.getName())
                        .description("Your accuracy in " + subject.getName() + " is " + String.format("%.1f", accuracy)
                                + "%. A mock test will help identify remaining gaps.")
                        .priority(LearningPriority.MEDIUM)
                        .relatedSubject(subject)
                        .generatedAt(LocalDateTime.now())
                        .build());
            }
        }

        List<LearningHistory> recentHistory = learningHistoryRepository
                .findByStudentProfileIdAndRecordedAtAfter(studentProfile.getId(), LocalDateTime.now().minusDays(7));
        if (recentHistory.isEmpty()) {
            recommendations.add(Recommendation.builder()
                    .studentProfile(studentProfile)
                    .recommendationType(RecommendationType.STUDY_PLAN)
                    .title("Study reminder")
                    .description("You haven't studied in the last 7 days. Consistent practice is key to success.")
                    .priority(LearningPriority.MEDIUM)
                    .generatedAt(LocalDateTime.now())
                    .build());
        }

        if (enrolledSubjects.isEmpty()) {
            recommendations.add(Recommendation.builder()
                    .studentProfile(studentProfile)
                    .recommendationType(RecommendationType.STUDY_PLAN)
                    .title("Create a study plan")
                    .description("Enroll in subjects and start a structured study plan for better academic performance.")
                    .priority(LearningPriority.LOW)
                    .generatedAt(LocalDateTime.now())
                    .build());
        }

        recommendationRepository.saveAll(recommendations);

        return recommendationRepository.findByStudentProfileIdOrderByGeneratedAtDesc(studentProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RecommendationResponse> getRecommendations(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        return recommendationRepository.findByStudentProfileIdOrderByGeneratedAtDesc(studentProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        recommendation.setIsRead(true);
        recommendationRepository.save(recommendation);
    }

    @Override
    @Transactional
    public void acceptRecommendation(Long recommendationId) {
        Recommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation", "id", recommendationId));
        recommendation.setIsAccepted(true);
        recommendation.setIsRead(true);
        recommendationRepository.save(recommendation);
    }

    private RecommendationResponse mapToResponse(Recommendation rec) {
        return RecommendationResponse.builder()
                .id(rec.getId())
                .type(rec.getRecommendationType() != null ? rec.getRecommendationType().name() : null)
                .title(rec.getTitle())
                .description(rec.getDescription())
                .priority(rec.getPriority() != null ? rec.getPriority().name() : null)
                .subjectName(rec.getRelatedSubject() != null ? rec.getRelatedSubject().getName() : null)
                .topicName(rec.getRelatedTopic() != null ? rec.getRelatedTopic().getName() : null)
                .unitName(rec.getRelatedUnit() != null ? rec.getRelatedUnit().getName() : null)
                .isRead(rec.getIsRead())
                .generatedAt(rec.getGeneratedAt())
                .build();
    }
}
