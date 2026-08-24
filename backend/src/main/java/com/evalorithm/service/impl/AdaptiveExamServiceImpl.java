package com.evalorithm.service.impl;

import com.evalorithm.dto.request.AdaptiveExamRequest;
import com.evalorithm.dto.response.AdaptiveExamResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.ExamStatus;
import com.evalorithm.enums.ExamType;
import com.evalorithm.enums.QuestionDifficulty;
import com.evalorithm.enums.QuestionType;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.AdaptiveExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdaptiveExamServiceImpl implements AdaptiveExamService {

    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final UserRepository userRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    private static final int TARGET_QUESTIONS = 25;
    private static final int QUESTIONS_PER_DIFFICULTY = 8;
    private static final Map<QuestionDifficulty, QuestionDifficulty> HARDER = Map.of(
            QuestionDifficulty.EASY, QuestionDifficulty.MEDIUM,
            QuestionDifficulty.MEDIUM, QuestionDifficulty.HARD,
            QuestionDifficulty.HARD, QuestionDifficulty.HARD
    );
    private static final Map<QuestionDifficulty, QuestionDifficulty> EASIER = Map.of(
            QuestionDifficulty.EASY, QuestionDifficulty.EASY,
            QuestionDifficulty.MEDIUM, QuestionDifficulty.EASY,
            QuestionDifficulty.HARD, QuestionDifficulty.MEDIUM
    );

    @Override
    @Transactional
    public AdaptiveExamResponse createAdaptiveExam(AdaptiveExamRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
        User creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getCreatedBy()));

        int totalQuestions = Math.max(10, Math.min(50, request.getTotalQuestions()));

        List<Question> allQuestions = questionRepository.findBySubjectIdAndStatus(subject.getId(), 
                com.evalorithm.enums.QuestionStatus.APPROVED);

        if (allQuestions.size() < totalQuestions) {
            throw new IllegalStateException("Not enough approved questions for this subject. Need " + totalQuestions + ", found " + allQuestions.size());
        }

        Map<QuestionDifficulty, List<Question>> questionsByDifficulty = allQuestions.stream()
                .filter(q -> q.getDifficulty() != null)
                .collect(Collectors.groupingBy(Question::getDifficulty));

        Exam exam = Exam.builder()
                .title(subject.getName() + " - Adaptive Quiz (" + totalQuestions + " Q)")
                .description("Adaptive exam generated from syllabus. Difficulty adjusts based on performance.")
                .examType(ExamType.PRACTICE_TEST)
                .status(ExamStatus.PUBLISHED)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(30))
                .durationMinutes(Math.max(30, totalQuestions * 2))
                .totalMarks(totalQuestions)
                .passingMarks((int) Math.ceil(totalQuestions * 0.4))
                .maxAttempts(3)
                .negativeMarksEnabled(false)
                .negativeMarksValue(0.0)
                .randomizeQuestions(false)
                .randomizeOptions(true)
                .showResultsImmediately(true)
                .autoSubmit(true)
                .fullscreenRequired(true)
                .preventTabSwitch(true)
                .department(department)
                .semester(semester)
                .subject(subject)
                .createdBy(creator)
                .build();

        exam = examRepository.save(exam);

        List<ExamQuestion> examQuestions = new ArrayList<>();
        int order = 1;

        List<Question> easyQuestions = getQuestionsByDifficulty(questionsByDifficulty, QuestionDifficulty.EASY, totalQuestions / 2);
        for (Question q : easyQuestions) {
            examQuestions.add(buildExamQuestion(exam, q, order++));
        }

        List<Question> mediumQuestions = getQuestionsByDifficulty(questionsByDifficulty, QuestionDifficulty.MEDIUM, totalQuestions / 3);
        for (Question q : mediumQuestions) {
            examQuestions.add(buildExamQuestion(exam, q, order++));
        }

        List<Question> hardQuestions = getQuestionsByDifficulty(questionsByDifficulty, QuestionDifficulty.HARD, totalQuestions / 4);
        for (Question q : hardQuestions) {
            examQuestions.add(buildExamQuestion(exam, q, order++));
        }

        Collections.shuffle(examQuestions);
        for (int i = 0; i < examQuestions.size(); i++) {
            examQuestions.get(i).setOrderNumber(i + 1);
        }

        examQuestionRepository.saveAll(examQuestions);

        return AdaptiveExamResponse.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .totalQuestions(examQuestions.size())
                .durationMinutes(exam.getDurationMinutes())
                .message("Adaptive exam created successfully. Starts with easy questions, adapts based on your answers.")
                .build();
    }

    @Override
    @Transactional
    public AdaptiveExamResponse.AdaptiveQuestion getNextAdaptiveQuestion(Long attemptId, boolean previousCorrect, Long previousQuestionId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getIsActive()) {
            throw new IllegalStateException("Attempt is not active");
        }

        Exam exam = attempt.getExam();
        int answeredCount = (int) studentAnswerRepository.findByAttemptId(attempt.getId()).size();

        if (answeredCount >= exam.getTotalMarks()) {
            throw new IllegalStateException("All questions answered");
        }

        QuestionDifficulty targetDifficulty = determineNextDifficulty(attempt, previousCorrect);

        List<ExamQuestion> availableQuestions = examQuestionRepository.findByExamIdOrderByOrderNumberAsc(exam.getId());
        List<Long> answeredIds = studentAnswerRepository.findByAttemptId(attempt.getId()).stream()
                .map(ans -> ans.getExamQuestion().getId())
                .collect(Collectors.toList());

        List<ExamQuestion> unanswered = availableQuestions.stream()
                .filter(q -> q.getIsActive())
                .filter(q -> !answeredIds.contains(q.getId()))
                .filter(q -> q.getQuestion().getDifficulty() == targetDifficulty)
                .toList();

        if (unanswered.isEmpty()) {
            unanswered = availableQuestions.stream()
                    .filter(q -> q.getIsActive())
                    .filter(q -> !answeredIds.contains(q.getId()))
                    .toList();
        }

        if (unanswered.isEmpty()) {
            throw new IllegalStateException("No more questions available");
        }

        Collections.shuffle(unanswered);
        ExamQuestion nextQuestion = unanswered.get(0);

        return buildAdaptiveQuestionResponse(nextQuestion);
    }

    private QuestionDifficulty determineNextDifficulty(ExamAttempt attempt, boolean previousCorrect) {
        int answeredCount = (int) studentAnswerRepository.findByAttemptId(attempt.getId()).size();

        if (answeredCount == 0) {
            return QuestionDifficulty.EASY;
        }

        List<StudentAnswer> recentAnswers = studentAnswerRepository.findByAttemptId(attempt.getId());
        long correctCount = recentAnswers.stream().filter(StudentAnswer::getIsCorrect).count();
        double accuracy = (double) correctCount / recentAnswers.size();

        QuestionDifficulty currentDifficulty = QuestionDifficulty.EASY;
        if (attempt.getStudentAnswers() != null && !attempt.getStudentAnswers().isEmpty()) {
            StudentAnswer lastAnswer = attempt.getStudentAnswers().get(attempt.getStudentAnswers().size() - 1);
            if (lastAnswer.getExamQuestion() != null && lastAnswer.getExamQuestion().getQuestion() != null) {
                currentDifficulty = lastAnswer.getExamQuestion().getQuestion().getDifficulty();
            }
        }

        if (previousCorrect) {
            return HARDER.getOrDefault(currentDifficulty, QuestionDifficulty.HARD);
        } else {
            if (accuracy < 0.3) {
                return EASIER.getOrDefault(currentDifficulty, QuestionDifficulty.EASY);
            }
            return currentDifficulty;
        }
    }

    private List<Question> getQuestionsByDifficulty(Map<QuestionDifficulty, List<Question>> map, QuestionDifficulty diff, int count) {
        return map.getOrDefault(diff, Collections.emptyList()).stream()
                .limit(count)
                .toList();
    }

    private ExamQuestion buildExamQuestion(Exam exam, Question question, int order) {
        return ExamQuestion.builder()
                .exam(exam)
                .question(question)
                .orderNumber(order)
                .marks(1)
                .isActive(true)
                .build();
    }

    private AdaptiveExamResponse.AdaptiveQuestion buildAdaptiveQuestionResponse(ExamQuestion eq) {
        Question question = eq.getQuestion();
        AdaptiveExamResponse.AdaptiveQuestion.AdaptiveQuestionBuilder builder = AdaptiveExamResponse.AdaptiveQuestion.builder()
                .examQuestionId(eq.getId())
                .orderNumber(eq.getOrderNumber())
                .questionTitle(question.getTitle())
                .questionDescription(question.getDescription())
                .questionType(question.getQuestionType().name())
                .marks(eq.getMarks())
                .difficulty(question.getDifficulty() != null ? question.getDifficulty().name() : "MEDIUM");

        if (question.getQuestionType() == QuestionType.MCQ && question.getMcqOptions() != null) {
            List<AdaptiveExamResponse.LiveExamOption> options = question.getMcqOptions().stream()
                    .map(opt -> AdaptiveExamResponse.LiveExamOption.builder()
                            .optionLabel(opt.getOptionLabel())
                            .optionText(opt.getOptionText())
                            .build())
                    .toList();
            builder.options(options);
        }

        return builder.build();
    }
}
