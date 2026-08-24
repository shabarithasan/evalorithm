package com.evalorithm.service.impl;

import com.evalorithm.dto.response.PredictionResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.RiskLevel;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.PredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PredictionServiceImpl implements PredictionService {

    private final PredictionRepository predictionRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;
    private final ExamResultRepository examResultRepository;
    private final AdaptiveSessionRepository adaptiveSessionRepository;
    private final LearningHistoryRepository learningHistoryRepository;

    private static final double EXAM_WEIGHT = 0.40;
    private static final double ADAPTIVE_WEIGHT = 0.30;
    private static final double PRACTICE_WEIGHT = 0.15;
    private static final double TIME_WEIGHT = 0.15;

    @Override
    @Transactional
    public PredictionResponse predictPerformance(Long studentId, Long subjectId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));

        List<ExamResult> examResults = examResultRepository.findByStudentProfileId(studentProfile.getId());
        List<com.evalorithm.entity.AdaptiveSession> adaptiveSessions = adaptiveSessionRepository
                .findByStudentProfileId(studentProfile.getId());
        List<LearningHistory> learningHistoryList = learningHistoryRepository
                .findByStudentProfileIdAndSubjectId(studentProfile.getId(), subjectId);

        double examScore = examResults.stream()
                .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                .average()
                .orElse(50.0);

        double adaptiveAccuracy = adaptiveSessions.stream()
                .filter(s -> s.getQuestionsAnswered() > 0)
                .mapToDouble(s -> (double) s.getCorrectAnswers() / s.getQuestionsAnswered() * 100)
                .average()
                .orElse(50.0);

        double practiceRate = Math.min(learningHistoryList.size() / 10.0, 1.0) * 100;

        double timeSpent = learningHistoryList.stream()
                .mapToInt(lh -> lh.getTimeSpentMinutes() != null ? lh.getTimeSpentMinutes() : 0)
                .sum();
        double timeScore = Math.min(timeSpent / 600.0, 1.0) * 100;

        int totalAttempts = examResults.size() + adaptiveSessions.size();

        double weightedScore = (examScore * EXAM_WEIGHT)
                + (adaptiveAccuracy * ADAPTIVE_WEIGHT)
                + (practiceRate * PRACTICE_WEIGHT)
                + (timeScore * TIME_WEIGHT);

        double predictedMarks = weightedScore;
        String predictedGrade = calculateGrade(predictedMarks);
        double passProbability = Math.min(Math.max(weightedScore, 0), 100);
        RiskLevel riskLevel = calculateRiskLevel(passProbability);
        double confidenceLevel = Math.min(0.5 + (totalAttempts * 0.05), 0.95);

        String suggestedImprovement = generateSuggestedImprovement(examScore, adaptiveAccuracy, practiceRate);

        Prediction prediction = predictionRepository
                .findByStudentProfileIdAndSubjectId(studentProfile.getId(), subjectId)
                .orElse(Prediction.builder()
                        .studentProfile(studentProfile)
                        .subject(subject)
                        .build());

        prediction.setPredictedMarks(Math.round(predictedMarks * 100.0) / 100.0);
        prediction.setPredictedGrade(predictedGrade);
        prediction.setPassProbability(Math.round(passProbability * 100.0) / 100.0);
        prediction.setRiskLevel(riskLevel);
        prediction.setSuggestedImprovement(suggestedImprovement);
        prediction.setConfidenceLevel(Math.round(confidenceLevel * 100.0) / 100.0);
        prediction.setBasedOnAttempts(totalAttempts);
        prediction.setGeneratedAt(LocalDateTime.now());

        prediction = predictionRepository.save(prediction);

        return mapToResponse(prediction);
    }

    @Override
    public List<PredictionResponse> getPredictions(Long studentId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        return predictionRepository.findByStudentProfileId(studentProfile.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<PredictionResponse> getRiskStudents(Long subjectId) {
        List<Prediction> predictions = predictionRepository.findAll();

        return predictions.stream()
                .filter(p -> p.getSubject() != null && p.getSubject().getId().equals(subjectId))
                .filter(p -> p.getRiskLevel() == RiskLevel.HIGH || p.getRiskLevel() == RiskLevel.VERY_HIGH)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private String calculateGrade(double score) {
        if (score >= 90) return "A+";
        if (score >= 80) return "A";
        if (score >= 70) return "B+";
        if (score >= 60) return "B";
        if (score >= 50) return "C+";
        if (score >= 40) return "C";
        if (score >= 30) return "D";
        return "F";
    }

    private RiskLevel calculateRiskLevel(double passProbability) {
        if (passProbability < 30) return RiskLevel.VERY_HIGH;
        if (passProbability < 50) return RiskLevel.HIGH;
        if (passProbability < 70) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private String generateSuggestedImprovement(double examScore, double adaptiveAccuracy, double practiceRate) {
        StringBuilder sb = new StringBuilder();
        if (examScore < 50) {
            sb.append("Focus on reviewing past exam questions and understanding key concepts. ");
        }
        if (adaptiveAccuracy < 50) {
            sb.append("Practice more adaptive tests to strengthen weak areas. ");
        }
        if (practiceRate < 30) {
            sb.append("Increase practice frequency - aim for regular daily practice sessions. ");
        }
        if (sb.isEmpty()) {
            sb.append("Good performance overall. Continue maintaining consistent study habits.");
        }
        return sb.toString().trim();
    }

    private PredictionResponse mapToResponse(Prediction prediction) {
        return PredictionResponse.builder()
                .id(prediction.getId())
                .subjectName(prediction.getSubject() != null ? prediction.getSubject().getName() : null)
                .predictedMarks(prediction.getPredictedMarks())
                .predictedGrade(prediction.getPredictedGrade())
                .passProbability(prediction.getPassProbability())
                .riskLevel(prediction.getRiskLevel() != null ? prediction.getRiskLevel().name() : null)
                .suggestedImprovement(prediction.getSuggestedImprovement())
                .confidenceLevel(prediction.getConfidenceLevel())
                .generatedAt(prediction.getGeneratedAt())
                .build();
    }
}
