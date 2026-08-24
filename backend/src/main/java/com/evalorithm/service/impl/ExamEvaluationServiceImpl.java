package com.evalorithm.service.impl;

import com.evalorithm.entity.*;
import com.evalorithm.enums.QuestionType;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamEvaluationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExamEvaluationServiceImpl implements ExamEvaluationService {

    private final StudentAnswerRepository studentAnswerRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final MCQOptionRepository mcqOptionRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void evaluateAttempt(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamIdAndIsActiveTrue(attempt.getExam().getId());

        for (ExamQuestion eq : examQuestions) {
            StudentAnswer answer = answers.stream()
                    .filter(a -> a.getExamQuestion().getId().equals(eq.getId()))
                    .findFirst()
                    .orElse(null);

            if (answer == null || answer.getAnswerStatus() == com.evalorithm.enums.AnswerStatus.NOT_ANSWERED) {
                if (answer == null) {
                    answer = StudentAnswer.builder()
                            .attempt(attempt)
                            .examQuestion(eq)
                            .answerStatus(com.evalorithm.enums.AnswerStatus.NOT_ANSWERED)
                            .marksAwarded(0.0)
                            .build();
                    studentAnswerRepository.save(answer);
                }
                continue;
            }

            Question question = eq.getQuestion();
            boolean isCorrect = false;

            switch (question.getQuestionType()) {
                case MCQ -> isCorrect = evaluateMCQ(answer, question);
                case TRUE_FALSE -> isCorrect = evaluateTrueFalse(answer, question);
                case MATCH_FOLLOWING -> isCorrect = evaluateMatchFollowing(answer, question);
                case FILL_BLANKS -> isCorrect = evaluateFillBlanks(answer, question);
                case ASSERTION_REASON -> isCorrect = evaluateAssertionReason(answer, question);
                case PROGRAMMING -> isCorrect = evaluateProgramming(answer, question);
                default -> isCorrect = false;
            }

            answer.setIsCorrect(isCorrect);
            answer.setMarksAwarded(isCorrect ? (double) eq.getMarks() : 0.0);
            studentAnswerRepository.save(answer);
        }
    }

    private boolean evaluateMCQ(StudentAnswer answer, Question question) {
        if (answer.getSelectedOptionLabel() == null) return false;
        List<MCQOption> options = mcqOptionRepository.findByQuestionId(question.getId());
        return options.stream()
                .filter(MCQOption::getIsCorrect)
                .anyMatch(opt -> opt.getOptionLabel().equalsIgnoreCase(answer.getSelectedOptionLabel()));
    }

    private boolean evaluateTrueFalse(StudentAnswer answer, Question question) {
        if (answer.getTextAnswer() == null) return false;
        String correctAnswer = question.getDescription();
        if (correctAnswer != null && correctAnswer.contains("::")) {
            String[] parts = correctAnswer.split("::");
            if (parts.length > 1) {
                correctAnswer = parts[parts.length - 1].trim();
            }
        }
        return answer.getTextAnswer().trim().equalsIgnoreCase(correctAnswer != null ? correctAnswer.trim() : "");
    }

    private boolean evaluateMatchFollowing(StudentAnswer answer, Question question) {
        if (answer.getSelectedOptionIds() == null) return false;
        try {
            Map<String, String> studentMapping = objectMapper.readValue(
                    answer.getSelectedOptionIds(), new TypeReference<>() {});
            String description = question.getDescription();
            if (description == null) return false;
            Map<String, String> correctMapping = objectMapper.readValue(description, new TypeReference<>() {});
            return correctMapping.equals(studentMapping);
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    private boolean evaluateFillBlanks(StudentAnswer answer, Question question) {
        if (answer.getTextAnswer() == null) return false;
        String description = question.getDescription();
        if (description == null) return false;
        String correctAnswer;
        if (description.contains("::")) {
            String[] parts = description.split("::");
            correctAnswer = parts[parts.length - 1].trim();
        } else {
            correctAnswer = description.trim();
        }
        return answer.getTextAnswer().trim().equalsIgnoreCase(correctAnswer);
    }

    private boolean evaluateAssertionReason(StudentAnswer answer, Question question) {
        if (answer.getSelectedOptionLabel() == null) return false;
        String description = question.getDescription();
        if (description == null) return false;
        String correctOption;
        if (description.contains("::")) {
            String[] parts = description.split("::");
            correctOption = parts[parts.length - 1].trim();
        } else {
            return false;
        }
        return answer.getSelectedOptionLabel().equalsIgnoreCase(correctOption);
    }

    private boolean evaluateProgramming(StudentAnswer answer, Question question) {
        if (answer.getTextAnswer() == null) return false;
        ProgrammingQuestion pq = question.getProgrammingQuestion();
        if (pq == null || pq.getTestCases() == null) return false;
        try {
            List<Map<String, String>> testCases = objectMapper.readValue(
                    pq.getTestCases(), new TypeReference<>() {});
            if (testCases.isEmpty()) return false;
            String expectedOutput = testCases.get(0).get("expectedOutput");
            return answer.getTextAnswer().trim().equals(expectedOutput != null ? expectedOutput.trim() : "");
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    @Override
    @Transactional
    public ExamResult calculateResult(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        Exam exam = attempt.getExam();
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);

        double totalObtained = answers.stream()
                .mapToDouble(a -> a.getMarksAwarded() != null ? a.getMarksAwarded() : 0.0)
                .sum();

        int totalPossible = exam.getExamQuestions().stream()
                .filter(ExamQuestion::getIsActive)
                .mapToInt(ExamQuestion::getMarks)
                .sum();

        long correct = answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
        long wrong = answers.stream().filter(a -> Boolean.FALSE.equals(a.getIsCorrect())).count();
        long skipped = answers.stream()
                .filter(a -> a.getAnswerStatus() == com.evalorithm.enums.AnswerStatus.NOT_ANSWERED)
                .count();

        double percentage = totalPossible > 0 ? (totalObtained / totalPossible) * 100 : 0;
        String grade = getGrade(percentage);
        boolean passed = totalObtained >= exam.getPassingMarks();

        int timeTakenMinutes = (int) ChronoUnit.MINUTES.between(
                attempt.getStartTime(), attempt.getEndTime() != null ? attempt.getEndTime() : LocalDateTime.now());

        examResultRepository.findByExamIdAndStudentProfileId(exam.getId(), attempt.getStudentProfile().getId())
                .ifPresent(examResultRepository::delete);

        ExamResult result = ExamResult.builder()
                .exam(exam)
                .studentProfile(attempt.getStudentProfile())
                .attempt(attempt)
                .totalMarksObtained(totalObtained)
                .totalMarksPossible(totalPossible)
                .percentage(percentage)
                .grade(grade)
                .isPassed(passed)
                .correctAnswers((int) correct)
                .wrongAnswers((int) wrong)
                .skippedQuestions((int) skipped)
                .timeTakenMinutes(timeTakenMinutes)
                .evaluatedAt(LocalDateTime.now())
                .build();

        return examResultRepository.save(result);
    }

    @Override
    public String getGrade(double percentage) {
        if (percentage >= 90) return "A+";
        if (percentage >= 80) return "A";
        if (percentage >= 70) return "B+";
        if (percentage >= 60) return "B";
        if (percentage >= 50) return "C";
        if (percentage >= 40) return "D";
        return "F";
    }
}
