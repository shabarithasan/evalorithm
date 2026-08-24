package com.evalorithm.service.impl;

import com.evalorithm.dto.request.AdaptiveAnswerRequest;
import com.evalorithm.dto.response.AdaptiveQuestionResponse;
import com.evalorithm.dto.response.AdaptiveSessionResponse;
import com.evalorithm.dto.response.QuestionDifficultyHistoryResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.AIDifficulty;
import com.evalorithm.enums.QuestionDifficulty;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.AdaptiveTestingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdaptiveTestingServiceImpl implements AdaptiveTestingService {

    private final AdaptiveSessionRepository adaptiveSessionRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final QuestionDifficultyHistoryRepository questionDifficultyHistoryRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private static final int MAX_QUESTIONS = 50;

    @Override
    @Transactional
    public AdaptiveSessionResponse startAdaptiveSession(Long studentId, Long subjectId) {
        StudentProfile studentProfile = studentProfileRepository.findByUserId(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", studentId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));

        Optional<AdaptiveSession> existingActive = adaptiveSessionRepository
                .findByStudentProfileIdAndIsActiveTrue(studentProfile.getId());
        if (existingActive.isPresent()) {
            throw new BadRequestException("An active session already exists. Please end it first.");
        }

        AdaptiveSession session = AdaptiveSession.builder()
                .studentProfile(studentProfile)
                .subject(subject)
                .currentDifficulty(AIDifficulty.MEDIUM)
                .questionsAnswered(0)
                .correctAnswers(0)
                .wrongAnswers(0)
                .streakCount(0)
                .maxStreak(0)
                .isActive(true)
                .startTime(LocalDateTime.now())
                .difficultyHistory("[\"MEDIUM\"]")
                .build();

        session = adaptiveSessionRepository.save(session);
        return mapToResponse(session);
    }

    @Override
    public AdaptiveQuestionResponse getNextQuestion(Long sessionId) {
        AdaptiveSession session = adaptiveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("AdaptiveSession", "id", sessionId));

        if (!session.getIsActive()) {
            throw new BadRequestException("Session is no longer active.");
        }

        if (session.getQuestionsAnswered() >= MAX_QUESTIONS) {
            throw new BadRequestException("Maximum question limit (" + MAX_QUESTIONS + ") reached.");
        }

        List<Long> answeredQuestionIds = questionDifficultyHistoryRepository.findBySessionId(sessionId)
                .stream()
                .map(h -> h.getQuestion().getId())
                .collect(Collectors.toList());

        QuestionDifficulty mappedDifficulty = mapDifficulty(session.getCurrentDifficulty());

        List<Question> candidates = questionRepository.findAll().stream()
                .filter(q -> q.getSubject() != null && q.getSubject().getId().equals(session.getSubject().getId()))
                .filter(q -> q.getDifficulty() != null && q.getDifficulty() == mappedDifficulty)
                .filter(q -> !answeredQuestionIds.contains(q.getId()))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = questionRepository.findAll().stream()
                    .filter(q -> q.getSubject() != null && q.getSubject().getId().equals(session.getSubject().getId()))
                    .filter(q -> !answeredQuestionIds.contains(q.getId()))
                    .collect(Collectors.toList());
        }

        if (candidates.isEmpty()) {
            throw new BadRequestException("No more questions available for this subject.");
        }

        Question question = candidates.get(new Random().nextInt(candidates.size()));
        return mapToQuestionResponse(question, session);
    }

    @Override
    @Transactional
    public AdaptiveQuestionResponse submitAnswer(Long sessionId, AdaptiveAnswerRequest request) {
        AdaptiveSession session = adaptiveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("AdaptiveSession", "id", sessionId));

        if (!session.getIsActive()) {
            throw new BadRequestException("Session is no longer active.");
        }

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", request.getQuestionId()));

        boolean isCorrect = evaluateAnswer(question, request);

        QuestionDifficultyHistory history = QuestionDifficultyHistory.builder()
                .session(session)
                .question(question)
                .difficulty(session.getCurrentDifficulty())
                .wasCorrect(isCorrect)
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .answeredAt(LocalDateTime.now())
                .build();
        questionDifficultyHistoryRepository.save(history);

        session.setQuestionsAnswered(session.getQuestionsAnswered() + 1);
        if (isCorrect) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
            session.setStreakCount(session.getStreakCount() + 1);
            session.setMaxStreak(Math.max(session.getMaxStreak(), session.getStreakCount()));

            if (session.getStreakCount() >= 2) {
                session.setCurrentDifficulty(getNextHigherDifficulty(session.getCurrentDifficulty()));
                session.setStreakCount(0);
                updateDifficultyHistory(session, "UP");
            }
        } else {
            session.setWrongAnswers(session.getWrongAnswers() + 1);
            session.setStreakCount(0);
            session.setCurrentDifficulty(getNextLowerDifficulty(session.getCurrentDifficulty()));
            updateDifficultyHistory(session, "DOWN");
        }

        adaptiveSessionRepository.save(session);

        if (session.getQuestionsAnswered() >= MAX_QUESTIONS) {
            return null;
        }

        return getNextQuestion(sessionId);
    }

    @Override
    @Transactional
    public AdaptiveSessionResponse endSession(Long sessionId) {
        AdaptiveSession session = adaptiveSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("AdaptiveSession", "id", sessionId));

        session.setIsActive(false);
        session.setEndTime(LocalDateTime.now());

        double score = session.getQuestionsAnswered() > 0
                ? (double) session.getCorrectAnswers() / session.getQuestionsAnswered() * 100
                : 0.0;
        session.setFinalScore(Math.round(score * 100.0) / 100.0);

        session = adaptiveSessionRepository.save(session);
        return mapToResponse(session);
    }

    @Override
    public List<QuestionDifficultyHistoryResponse> getSessionHistory(Long sessionId) {
        if (!adaptiveSessionRepository.existsById(sessionId)) {
            throw new ResourceNotFoundException("AdaptiveSession", "id", sessionId);
        }

        return questionDifficultyHistoryRepository.findBySessionId(sessionId)
                .stream()
                .map(h -> QuestionDifficultyHistoryResponse.builder()
                        .id(h.getId())
                        .questionId(h.getQuestion().getId())
                        .difficulty(h.getDifficulty() != null ? h.getDifficulty().name() : null)
                        .wasCorrect(h.getWasCorrect())
                        .timeTakenSeconds(h.getTimeTakenSeconds())
                        .answeredAt(h.getAnsweredAt())
                        .build())
                .collect(Collectors.toList());
    }

    private boolean evaluateAnswer(Question question, AdaptiveAnswerRequest request) {
        if (request.getSelectedOption() != null && question.getMcqOptions() != null) {
            return question.getMcqOptions().stream()
                .filter(MCQOption::getIsCorrect)
                .anyMatch(opt -> opt.getOptionText().equalsIgnoreCase(request.getSelectedOption()));
        }
        if (request.getTextAnswer() != null) {
            return request.getTextAnswer().equalsIgnoreCase(question.getTitle());
        }
        return false;
    }

    private QuestionDifficulty mapDifficulty(AIDifficulty aiDifficulty) {
        return switch (aiDifficulty) {
            case EASY -> QuestionDifficulty.EASY;
            case MEDIUM -> QuestionDifficulty.MEDIUM;
            case HARD -> QuestionDifficulty.HARD;
            case EXPERT -> QuestionDifficulty.EXPERT;
        };
    }

    private AIDifficulty getNextHigherDifficulty(AIDifficulty current) {
        return switch (current) {
            case EASY -> AIDifficulty.MEDIUM;
            case MEDIUM -> AIDifficulty.HARD;
            case HARD -> AIDifficulty.EXPERT;
            case EXPERT -> AIDifficulty.EXPERT;
        };
    }

    private AIDifficulty getNextLowerDifficulty(AIDifficulty current) {
        return switch (current) {
            case EXPERT -> AIDifficulty.HARD;
            case HARD -> AIDifficulty.MEDIUM;
            case MEDIUM -> AIDifficulty.EASY;
            case EASY -> AIDifficulty.EASY;
        };
    }

    private void updateDifficultyHistory(AdaptiveSession session, String direction) {
        String current = session.getDifficultyHistory();
        if (current == null) current = "[]";
        try {
            List<String> history = new ArrayList<>(objectMapper.readValue(current,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
            history.add(session.getCurrentDifficulty().name() + "_" + direction);
            session.setDifficultyHistory(objectMapper.writeValueAsString(history));
        } catch (Exception e) {
            session.setDifficultyHistory("[\"" + session.getCurrentDifficulty().name() + "\"]");
        }
    }

    private AdaptiveSessionResponse mapToResponse(AdaptiveSession session) {
        double accuracy = session.getQuestionsAnswered() > 0
                ? (double) session.getCorrectAnswers() / session.getQuestionsAnswered() * 100
                : 0.0;

        return AdaptiveSessionResponse.builder()
                .id(session.getId())
                .subjectName(session.getSubject() != null ? session.getSubject().getName() : null)
                .currentDifficulty(session.getCurrentDifficulty() != null ? session.getCurrentDifficulty().name() : null)
                .questionsAnswered(session.getQuestionsAnswered())
                .correctAnswers(session.getCorrectAnswers())
                .wrongAnswers(session.getWrongAnswers())
                .accuracy(Math.round(accuracy * 100.0) / 100.0)
                .streakCount(session.getStreakCount())
                .maxStreak(session.getMaxStreak())
                .isActive(session.getIsActive())
                .score(session.getFinalScore())
                .startTime(session.getStartTime())
                .build();
    }

    private AdaptiveQuestionResponse mapToQuestionResponse(Question question, AdaptiveSession session) {
        List<String> options = null;
        if (question.getMcqOptions() != null && !question.getMcqOptions().isEmpty()) {
            options = question.getMcqOptions().stream()
                    .map(MCQOption::getOptionText)
                    .collect(Collectors.toList());
        }

        return AdaptiveQuestionResponse.builder()
                .questionId(question.getId())
                .questionText(question.getTitle())
                .questionType(question.getQuestionType() != null ? question.getQuestionType().name() : null)
                .difficulty(session.getCurrentDifficulty() != null ? session.getCurrentDifficulty().name() : null)
                .options(options)
                .marks(question.getMarks())
                .timeLimit(question.getEstimatedTime())
                .build();
    }
}
