package com.evalorithm.service.impl;

import com.evalorithm.dto.request.StudentAnswerRequest;
import com.evalorithm.dto.response.LiveExamResponse;
import com.evalorithm.dto.response.SubmitExamResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.AnswerStatus;
import com.evalorithm.enums.ExamStatus;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamEvaluationService;
import com.evalorithm.service.ExamTakingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamTakingServiceImpl implements ExamTakingService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamStudentRepository examStudentRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ExamEvaluationService examEvaluationService;

    @Override
    @Transactional
    public LiveExamResponse startExam(Long examId, Long studentId, String ip, String userAgent) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        if (exam.getStatus() != ExamStatus.PUBLISHED && exam.getStatus() != ExamStatus.ACTIVE) {
            throw new BadRequestException("Exam is not available for taking");
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartDate()) || now.isAfter(exam.getEndDate())) {
            throw new BadRequestException("Exam is not within the scheduled time window");
        }

        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "id", studentId));

        if (!examStudentRepository.existsByExamIdAndStudentProfileId(examId, studentId)) {
            throw new BadRequestException("You are not assigned to this exam");
        }

        List<ExamAttempt> previousAttempts = examAttemptRepository.findByExamIdAndStudentProfileId(examId, studentId);
        long completedAttempts = previousAttempts.stream()
                .filter(a -> !a.getIsActive())
                .count();

        if (completedAttempts >= exam.getMaxAttempts()) {
            throw new BadRequestException("Maximum attempts exceeded");
        }

        ExamAttempt activeAttempt = previousAttempts.stream()
                .filter(ExamAttempt::getIsActive)
                .findFirst()
                .orElse(null);

        if (activeAttempt != null) {
            return buildLiveExamResponse(exam, activeAttempt);
        }

        ExamAttempt attempt = ExamAttempt.builder()
                .exam(exam)
                .studentProfile(student)
                .startTime(now)
                .isActive(true)
                .ipAddress(ip)
                .userAgent(userAgent)
                .build();
        attempt = examAttemptRepository.save(attempt);

        if (exam.getStatus() == ExamStatus.PUBLISHED) {
            exam.setStatus(ExamStatus.ACTIVE);
            examRepository.save(exam);
        }

        return buildLiveExamResponse(exam, attempt);
    }

    @Override
    public LiveExamResponse getExamQuestion(Long attemptId, int questionIndex) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getIsActive()) {
            throw new BadRequestException("This attempt is no longer active");
        }

        return buildLiveExamResponse(attempt.getExam(), attempt);
    }

    @Override
    @Transactional
    public void saveAnswer(Long attemptId, StudentAnswerRequest request) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getIsActive()) {
            throw new BadRequestException("This attempt is no longer active");
        }

        ExamQuestion examQuestion = examQuestionRepository.findById(request.getExamQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("ExamQuestion", "id", request.getExamQuestionId()));

        StudentAnswer studentAnswer = studentAnswerRepository.findByAttemptIdAndExamQuestionId(attemptId, request.getExamQuestionId())
                .orElse(StudentAnswer.builder()
                        .attempt(attempt)
                        .examQuestion(examQuestion)
                        .build());

        studentAnswer.setSelectedOptionLabel(request.getSelectedOptionLabel());
        studentAnswer.setSelectedOptionIds(request.getSelectedOptionIds());
        studentAnswer.setTextAnswer(request.getTextAnswer());
        studentAnswer.setTimeTakenSeconds(request.getTimeTakenSeconds());
        studentAnswer.setAnsweredAt(LocalDateTime.now());

        if (request.getSelectedOptionLabel() != null || request.getSelectedOptionIds() != null ||
            (request.getTextAnswer() != null && !request.getTextAnswer().isBlank())) {
            studentAnswer.setAnswerStatus(AnswerStatus.ANSWERED);
        } else {
            studentAnswer.setAnswerStatus(AnswerStatus.NOT_ANSWERED);
        }

        studentAnswerRepository.save(studentAnswer);
    }

    @Override
    @Transactional
    public SubmitExamResponse submitExam(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getIsActive()) {
            throw new BadRequestException("This attempt is already submitted");
        }

        attempt.setEndTime(LocalDateTime.now());
        attempt.setIsActive(false);
        examAttemptRepository.save(attempt);

        Exam exam = attempt.getExam();
        List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attemptId);

        int totalAnswered = (int) answers.stream()
                .filter(a -> a.getAnswerStatus() == AnswerStatus.ANSWERED || a.getAnswerStatus() == AnswerStatus.ANSWERED_MARKED)
                .count();
        int totalSkipped = (int) answers.stream()
                .filter(a -> a.getAnswerStatus() == AnswerStatus.NOT_ANSWERED)
                .count();

        boolean autoEvaluated = false;
        if (exam.getAutoSubmit()) {
            examEvaluationService.evaluateAttempt(attemptId);
            autoEvaluated = true;
        }

        long correct = studentAnswerRepository.countByAttemptIdAndIsCorrectTrue(attemptId);
        long wrong = studentAnswerRepository.countByAttemptIdAndIsCorrectFalse(attemptId);

        return SubmitExamResponse.builder()
                .attemptId(attemptId)
                .totalAnswered(totalAnswered)
                .totalCorrect((int) correct)
                .totalWrong((int) wrong)
                .totalSkipped(totalSkipped)
                .autoEvaluated(autoEvaluated)
                .message(autoEvaluated ? "Exam submitted and evaluated successfully" : "Exam submitted successfully")
                .build();
    }

    @Override
    @Transactional
    public LiveExamResponse resumeExam(Long attemptId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getIsActive()) {
            throw new BadRequestException("This attempt is no longer active");
        }

        Exam exam = attempt.getExam();
        long minutesElapsed = Duration.between(attempt.getStartTime(), LocalDateTime.now()).toMinutes();
        if (minutesElapsed >= exam.getDurationMinutes() && exam.getAutoSubmit()) {
            submitExam(attemptId);
            throw new BadRequestException("Exam time has expired and has been auto-submitted");
        }

        return buildLiveExamResponse(exam, attempt);
    }

    @Override
    public LiveExamResponse getExamStatus(Long examId, Long studentId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "id", studentId));

        ExamAttempt activeAttempt = examAttemptRepository.findByExamIdAndStudentProfileId(examId, studentId)
                .stream()
                .filter(ExamAttempt::getIsActive)
                .findFirst()
                .orElse(null);

        if (activeAttempt == null) {
            throw new BadRequestException("No active attempt found for this exam");
        }

        return buildLiveExamResponse(exam, activeAttempt);
    }

    private LiveExamResponse buildLiveExamResponse(Exam exam, ExamAttempt attempt) {
        List<ExamQuestion> examQuestions = examRandomizeOptions(exam)
                ? examQuestionRepository.findByExamIdAndIsActiveTrue(exam.getId())
                : examQuestionRepository.findByExamIdOrderByOrderNumberAsc(exam.getId());

        examQuestions = examQuestions.stream().filter(ExamQuestion::getIsActive).toList();

        List<LiveExamResponse.LiveExamQuestion> liveQuestions = new ArrayList<>();
        for (ExamQuestion eq : examQuestions) {
            Question question = eq.getQuestion();
            LiveExamResponse.LiveExamQuestion lq = LiveExamResponse.LiveExamQuestion.builder()
                    .examQuestionId(eq.getId())
                    .orderNumber(eq.getOrderNumber())
                    .questionType(question.getQuestionType())
                    .questionTitle(question.getTitle())
                    .questionDescription(question.getDescription())
                    .marks(eq.getMarks())
                    .build();

            if (question.getQuestionType() == com.evalorithm.enums.QuestionType.MCQ && question.getMcqOptions() != null) {
                List<LiveExamResponse.LiveExamOption> options = question.getMcqOptions().stream()
                        .map(opt -> LiveExamResponse.LiveExamOption.builder()
                                .optionLabel(opt.getOptionLabel())
                                .optionText(opt.getOptionText())
                                .build())
                        .toList();
                lq.setOptions(options);
            }

            liveQuestions.add(lq);
        }

        long timeElapsed = Duration.between(attempt.getStartTime(), LocalDateTime.now()).getSeconds();
        long totalDurationSeconds = (long) exam.getDurationMinutes() * 60;
        long timeRemaining = Math.max(0, totalDurationSeconds - timeElapsed);

        List<StudentAnswer> savedAnswers = studentAnswerRepository.findByAttemptId(attempt.getId());
        int currentIndex = savedAnswers.size();

        return LiveExamResponse.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .durationMinutes(exam.getDurationMinutes())
                .totalMarks(exam.getTotalMarks())
                .questions(liveQuestions)
                .currentQuestionIndex(Math.min(currentIndex, liveQuestions.size() - 1))
                .timeRemainingSeconds(timeRemaining)
                .attemptId(attempt.getId())
                .build();
    }

    private boolean examRandomizeOptions(Exam exam) {
        return exam.getRandomizeQuestions() != null && exam.getRandomizeQuestions();
    }
}
