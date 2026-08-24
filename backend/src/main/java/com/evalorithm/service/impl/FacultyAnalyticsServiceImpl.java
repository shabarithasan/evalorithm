package com.evalorithm.service.impl;

import com.evalorithm.dto.response.FacultyAnalyticsResponse;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.FacultyAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacultyAnalyticsServiceImpl implements FacultyAnalyticsService {

    private final FacultyProfileRepository facultyProfileRepository;
    private final FacultyAnalyticsRepository facultyAnalyticsRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    @Override
    public FacultyAnalyticsResponse calculateFacultyAnalytics(Long facultyId) {
        FacultyProfile facultyProfile = facultyProfileRepository.findByUserId(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("FacultyProfile", "userId", facultyId));

        String facultyName = "";
        if (facultyProfile.getUser() != null) {
            facultyName = (facultyProfile.getUser().getFirstName() != null ? facultyProfile.getUser().getFirstName() : "") +
                    (facultyProfile.getUser().getLastName() != null ? " " + facultyProfile.getUser().getLastName() : "");
        }

        List<Subject> assignedSubjects = facultyProfile.getAssignedSubjects();
        if (assignedSubjects == null || assignedSubjects.isEmpty()) {
            return FacultyAnalyticsResponse.builder()
                    .facultyId(facultyId)
                    .facultyName(facultyName.trim())
                    .totalExams(0)
                    .averageClassScore(0.0)
                    .totalStudents(0)
                    .passRate(0.0)
                    .build();
        }

        long totalExams = 0;
        double totalScore = 0;
        int scoreCount = 0;
        int totalStudents = 0;
        int totalPassed = 0;

        for (Subject subject : assignedSubjects) {
            List<Exam> exams = examRepository.findAll().stream()
                    .filter(e -> e.getSubject() != null && e.getSubject().getId().equals(subject.getId()))
                    .filter(e -> e.getCreatedBy() != null && e.getCreatedBy().getId().equals(facultyId))
                    .collect(Collectors.toList());

            totalExams += exams.size();

            for (Exam exam : exams) {
                List<ExamResult> results = examResultRepository.findByExamId(exam.getId());
                for (ExamResult result : results) {
                    if (result.getPercentage() != null) {
                        totalScore += result.getPercentage();
                        scoreCount++;
                    }
                    totalStudents++;
                    if (Boolean.TRUE.equals(result.getIsPassed())) {
                        totalPassed++;
                    }
                }
            }
        }

        FacultyAnalytics analytics = facultyAnalyticsRepository
                .findByFacultyProfileId(facultyProfile.getId())
                .orElse(FacultyAnalytics.builder()
                        .facultyProfile(facultyProfile)
                        .subject(assignedSubjects.get(0))
                        .build());

        analytics.setTotalExamsCreated((int) totalExams);
        analytics.setAverageClassScore(scoreCount > 0 ? Math.round(totalScore / scoreCount * 100.0) / 100.0 : 0.0);
        analytics.setTotalStudents(totalStudents);
        analytics.setPassRate(totalStudents > 0 ? Math.round((double) totalPassed / totalStudents * 10000.0) / 100.0 : 0.0);
        analytics.setLastCalculatedAt(LocalDateTime.now());
        facultyAnalyticsRepository.save(analytics);

        return FacultyAnalyticsResponse.builder()
                .facultyId(facultyId)
                .facultyName(facultyName.trim())
                .totalExams((int) totalExams)
                .averageClassScore(analytics.getAverageClassScore())
                .totalStudents(totalStudents)
                .passRate(analytics.getPassRate())
                .build();
    }

    @Override
    public Map<String, Object> getClassPerformance(Long facultyId, Long subjectId) {
        FacultyProfile facultyProfile = facultyProfileRepository.findByUserId(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("FacultyProfile", "userId", facultyId));

        List<Exam> exams = examRepository.findAll().stream()
                .filter(e -> e.getSubject() != null && e.getSubject().getId().equals(subjectId))
                .filter(e -> e.getCreatedBy() != null && e.getCreatedBy().getId().equals(facultyId))
                .collect(Collectors.toList());

        double avgScore = exams.stream()
                .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                .average()
                .orElse(0.0);

        long totalStudents = exams.stream()
                .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                .count();

        long passed = exams.stream()
                .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                .filter(er -> Boolean.TRUE.equals(er.getIsPassed()))
                .count();

        Map<String, Object> result = new HashMap<>();
        result.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
        result.put("totalStudents", totalStudents);
        result.put("passRate", totalStudents > 0 ? Math.round((double) passed / totalStudents * 10000.0) / 100.0 : 0.0);
        return result;
    }

    @Override
    public List<Map<String, Object>> getTopPerformers(Long facultyId, Long subjectId, int limit) {
        List<Exam> exams = examRepository.findAll().stream()
                .filter(e -> e.getSubject() != null && e.getSubject().getId().equals(subjectId))
                .filter(e -> e.getCreatedBy() != null && e.getCreatedBy().getId().equals(facultyId))
                .collect(Collectors.toList());

        Map<Long, Double> studentScores = new HashMap<>();
        Map<Long, String> studentNames = new HashMap<>();

        for (Exam exam : exams) {
            List<ExamResult> results = examResultRepository.findByExamId(exam.getId());
            for (ExamResult result : results) {
                Long studentId = result.getStudentProfile().getId();
                double score = result.getPercentage() != null ? result.getPercentage() : 0.0;
                studentScores.merge(studentId, score, Math::max);

                if (result.getStudentProfile().getUser() != null) {
                    User user = result.getStudentProfile().getUser();
                    String name = (user.getFirstName() != null ? user.getFirstName() : "") +
                            (user.getLastName() != null ? " " + user.getLastName() : "");
                    studentNames.putIfAbsent(studentId, name.trim());
                }
            }
        }

        return studentScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> performer = new HashMap<>();
                    performer.put("studentId", entry.getKey());
                    performer.put("studentName", studentNames.getOrDefault(entry.getKey(), "Unknown"));
                    performer.put("score", entry.getValue());
                    return performer;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getLowPerformers(Long facultyId, Long subjectId, int limit) {
        List<Map<String, Object>> allPerformers = getTopPerformers(facultyId, subjectId, Integer.MAX_VALUE);
        Collections.reverse(allPerformers);
        return allPerformers.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<FacultyAnalyticsResponse> getSubjectAnalysis(Long facultyId) {
        FacultyProfile facultyProfile = facultyProfileRepository.findByUserId(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("FacultyProfile", "userId", facultyId));

        String facultyName = "";
        if (facultyProfile.getUser() != null) {
            facultyName = (facultyProfile.getUser().getFirstName() != null ? facultyProfile.getUser().getFirstName() : "") +
                    (facultyProfile.getUser().getLastName() != null ? " " + facultyProfile.getUser().getLastName() : "");
        }

        List<FacultyAnalyticsResponse> results = new ArrayList<>();
        if (facultyProfile.getAssignedSubjects() != null) {
            for (Subject subject : facultyProfile.getAssignedSubjects()) {
                List<Exam> exams = examRepository.findAll().stream()
                        .filter(e -> e.getSubject() != null && e.getSubject().getId().equals(subject.getId()))
                        .filter(e -> e.getCreatedBy() != null && e.getCreatedBy().getId().equals(facultyId))
                        .collect(Collectors.toList());

                double avgScore = exams.stream()
                        .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                        .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                        .average()
                        .orElse(0.0);

                long totalStudents = exams.stream()
                        .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                        .count();

                long passed = exams.stream()
                        .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                        .filter(er -> Boolean.TRUE.equals(er.getIsPassed()))
                        .count();

                results.add(FacultyAnalyticsResponse.builder()
                        .facultyId(facultyId)
                        .facultyName(facultyName.trim())
                        .subjectName(subject.getName())
                        .totalExams(exams.size())
                        .averageClassScore(Math.round(avgScore * 100.0) / 100.0)
                        .totalStudents((int) totalStudents)
                        .passRate(totalStudents > 0 ? Math.round((double) passed / totalStudents * 10000.0) / 100.0 : 0.0)
                        .build());
            }
        }
        return results;
    }

    @Override
    public Map<String, Object> getQuestionDifficultyAnalysis(Long facultyId) {
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("distribution", Map.of(
                "EASY", 0,
                "MEDIUM", 0,
                "HARD", 0,
                "EXPERT", 0
        ));
        analysis.put("successRates", Map.of(
                "EASY", 0.0,
                "MEDIUM", 0.0,
                "HARD", 0.0,
                "EXPERT", 0.0
        ));
        return analysis;
    }
}
