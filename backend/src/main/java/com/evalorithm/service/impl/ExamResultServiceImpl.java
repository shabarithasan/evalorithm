package com.evalorithm.service.impl;

import com.evalorithm.dto.response.ExamResultResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.StudentAnswerResponse;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamResultService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamResultServiceImpl implements ExamResultService {

    private final ExamResultRepository examResultRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;

    @Override
    public ExamResultResponse getResult(Long examId, Long studentId) {
        ExamResult result = examResultRepository.findByExamIdAndStudentProfileId(examId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamResult", "examId/studentId", examId + "/" + studentId));
        return mapToResponse(result);
    }

    @Override
    public PageResponse<ExamResultResponse> getAllResultsForExam(Long examId, Pageable pageable) {
        Page<ExamResult> page = examResultRepository.findByExamId(examId, pageable);
        List<ExamResultResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public PageResponse<ExamResultResponse> getStudentResults(Long studentId, Pageable pageable) {
        Page<ExamResult> page = examResultRepository.findByStudentProfileId(studentId, pageable);
        List<ExamResultResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public List<StudentAnswerResponse> getResultDetails(Long resultId) {
        ExamResult result = examResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamResult", "id", resultId));

        ExamAttempt attempt = result.getAttempt();
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attempt.getId());

        return answers.stream()
                .map(this::mapToAnswerResponse)
                .toList();
    }

    @Override
    public byte[] exportResults(Long examId) {
        List<ExamResult> results = examResultRepository.findByExamId(examId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        writer.println("Student Name,Register Number,Total Marks Obtained,Total Marks Possible,Percentage,Grade,Pass/Fail,Correct Answers,Wrong Answers,Skipped,Time Taken (min)");

        for (ExamResult result : results) {
            StudentProfile sp = result.getStudentProfile();
            User user = sp.getUser();
            String studentName = (user.getFirstName() != null ? user.getFirstName() : "") +
                    (user.getLastName() != null ? " " + user.getLastName() : "");

            writer.printf("%s,%s,%.1f,%d,%.2f%%,%s,%s,%d,%d,%d,%d%n",
                    studentName.trim(),
                    sp.getRegisterNumber(),
                    result.getTotalMarksObtained(),
                    result.getTotalMarksPossible(),
                    result.getPercentage(),
                    result.getGrade(),
                    result.getIsPassed() ? "Pass" : "Fail",
                    result.getCorrectAnswers(),
                    result.getWrongAnswers(),
                    result.getSkippedQuestions(),
                    result.getTimeTakenMinutes());
        }

        writer.flush();
        return outputStream.toByteArray();
    }

    private ExamResultResponse mapToResponse(ExamResult result) {
        StudentProfile sp = result.getStudentProfile();
        User user = sp.getUser();
        String studentName = (user.getFirstName() != null ? user.getFirstName() : "") +
                (user.getLastName() != null ? " " + user.getLastName() : "");

        return ExamResultResponse.builder()
                .id(result.getId())
                .examTitle(result.getExam().getTitle())
                .studentName(studentName.trim())
                .totalMarksObtained(result.getTotalMarksObtained())
                .totalMarksPossible(result.getTotalMarksPossible())
                .percentage(result.getPercentage())
                .grade(result.getGrade())
                .isPassed(result.getIsPassed())
                .correctAnswers(result.getCorrectAnswers())
                .wrongAnswers(result.getWrongAnswers())
                .skippedQuestions(result.getSkippedQuestions())
                .timeTakenMinutes(result.getTimeTakenMinutes())
                .evaluatedAt(result.getEvaluatedAt())
                .build();
    }

    private StudentAnswerResponse mapToAnswerResponse(StudentAnswer answer) {
        ExamQuestion eq = answer.getExamQuestion();
        Question question = eq.getQuestion();

        return StudentAnswerResponse.builder()
                .id(answer.getId())
                .examQuestionId(eq.getId())
                .questionTitle(question.getTitle())
                .questionType(question.getQuestionType())
                .selectedOptionLabel(answer.getSelectedOptionLabel())
                .textAnswer(answer.getTextAnswer())
                .isCorrect(answer.getIsCorrect())
                .marksAwarded(answer.getMarksAwarded())
                .timeTakenSeconds(answer.getTimeTakenSeconds())
                .answerStatus(answer.getAnswerStatus())
                .build();
    }
}
