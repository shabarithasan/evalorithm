package com.evalorithm.service.impl;

import com.evalorithm.dto.response.ExamReportResponse;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExamReportServiceImpl implements ExamReportService {

    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamStudentRepository examStudentRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Override
    public ExamReportResponse getExamSummary(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamResult> results = examResultRepository.findByExamId(examId);
        int totalStudents = examStudentRepository.findByExamId(examId).size();
        int appeared = results.size();
        int passed = (int) results.stream().filter(ExamResult::getIsPassed).count();
        int failed = appeared - passed;

        double averageMarks = results.stream()
                .mapToDouble(r -> r.getTotalMarksObtained() != null ? r.getTotalMarksObtained() : 0.0)
                .average().orElse(0.0);

        double highestMarks = results.stream()
                .mapToDouble(r -> r.getTotalMarksObtained() != null ? r.getTotalMarksObtained() : 0.0)
                .max().orElse(0.0);

        double lowestMarks = results.stream()
                .mapToDouble(r -> r.getTotalMarksObtained() != null ? r.getTotalMarksObtained() : 0.0)
                .min().orElse(0.0);

        double passPercentage = totalStudents > 0 ? (double) passed / totalStudents * 100 : 0.0;

        return ExamReportResponse.builder()
                .examTitle(exam.getTitle())
                .totalStudents(totalStudents)
                .appeared(appeared)
                .passed(passed)
                .failed(failed)
                .averageMarks(averageMarks)
                .highestMarks(highestMarks)
                .lowestMarks(lowestMarks)
                .passPercentage(passPercentage)
                .build();
    }

    @Override
    public List<Map<String, Object>> getQuestionWiseReport(Long examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdAndIsActiveTrue(examId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (ExamQuestion eq : examQuestions) {
            Question question = eq.getQuestion();
            List<StudentAnswer> allAnswers = new ArrayList<>();

            List<ExamAttempt> attempts = new ArrayList<>();
            for (ExamStudent es : examStudentRepository.findByExamId(examId)) {
                attempts.addAll(examRepository.findById(examId).get().getExamAttempts());
            }

            Map<String, Object> questionReport = new LinkedHashMap<>();
            questionReport.put("questionId", question.getId());
            questionReport.put("questionTitle", question.getTitle());
            questionReport.put("questionType", question.getQuestionType());
            questionReport.put("marks", eq.getMarks());
            questionReport.put("orderNumber", eq.getOrderNumber());

            long totalAttempted = allAnswers.size();
            long correctCount = allAnswers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
            double correctPercentage = totalAttempted > 0 ? (double) correctCount / totalAttempted * 100 : 0.0;

            questionReport.put("totalAttempted", totalAttempted);
            questionReport.put("correctCount", correctCount);
            questionReport.put("wrongCount", totalAttempted - correctCount);
            questionReport.put("correctPercentage", correctPercentage);

            report.add(questionReport);
        }

        return report;
    }

    @Override
    public List<Map<String, Object>> getStudentWiseReport(Long examId) {
        examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamResult> results = examResultRepository.findByExamId(examId);
        List<Map<String, Object>> report = new ArrayList<>();

        for (ExamResult result : results) {
            StudentProfile sp = result.getStudentProfile();
            User user = sp.getUser();
            String studentName = (user.getFirstName() != null ? user.getFirstName() : "") +
                    (user.getLastName() != null ? " " + user.getLastName() : "");

            Map<String, Object> studentReport = new LinkedHashMap<>();
            studentReport.put("studentId", sp.getId());
            studentReport.put("studentName", studentName.trim());
            studentReport.put("registerNumber", sp.getRegisterNumber());
            studentReport.put("totalMarksObtained", result.getTotalMarksObtained());
            studentReport.put("totalMarksPossible", result.getTotalMarksPossible());
            studentReport.put("percentage", result.getPercentage());
            studentReport.put("grade", result.getGrade());
            studentReport.put("isPassed", result.getIsPassed());
            studentReport.put("correctAnswers", result.getCorrectAnswers());
            studentReport.put("wrongAnswers", result.getWrongAnswers());
            studentReport.put("skippedQuestions", result.getSkippedQuestions());
            studentReport.put("timeTakenMinutes", result.getTimeTakenMinutes());

            report.add(studentReport);
        }

        return report;
    }
}
