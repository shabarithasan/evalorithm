package com.evalorithm.service.impl;

import com.evalorithm.dto.request.AIQuestionGenerateRequest;
import com.evalorithm.dto.response.AIDashboardResponse;
import com.evalorithm.dto.response.AIQuestionResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.AIDifficulty;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.AIQuestionGeneratorService;
import com.evalorithm.util.QuestionTemplates;
import com.evalorithm.util.QuestionTemplates.QuestionTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AIQuestionGeneratorServiceImpl implements AIQuestionGeneratorService {

    private final AIQuestionRepository aiQuestionRepository;
    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final TopicRepository topicRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final StudentAnalyticsRepository studentAnalyticsRepository;
    private final RecommendationRepository recommendationRepository;
    private final QuestionTemplates questionTemplates;
    private final ObjectMapper objectMapper;

    private static final String MODEL_VERSION = "evalorithm-template-v1";

    @Override
    @Transactional
    public List<AIQuestionResponse> generateQuestions(AIQuestionGenerateRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        Unit unit = null;
        if (request.getUnitId() != null) {
            unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", request.getUnitId()));
        }

        Topic topic = null;
        if (request.getTopicId() != null) {
            topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", request.getTopicId()));
        }

        int count = request.getCount() != null ? Math.min(request.getCount(), 50) : 5;
        String difficulty = request.getDifficulty() != null ? request.getDifficulty() : "MEDIUM";
        String questionType = request.getQuestionType() != null ? request.getQuestionType() : "MCQ";

        List<AIQuestion> generated = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            QuestionTemplate template = questionTemplates.getRandomTemplate(questionType, difficulty);

            String topicName = topic != null ? topic.getName() : subject.getName();
            String unitName = unit != null ? unit.getName() : "";

            String questionText = questionTemplates.fillPlaceholders(template.getText(), subject.getName(), topicName, unitName);
            String explanation = questionTemplates.fillPlaceholders(template.getExplanation(), subject.getName(), topicName, unitName);

            String optionsJson = null;
            if ("MCQ".equalsIgnoreCase(questionType) || "MATCH_FOLLOWING".equalsIgnoreCase(questionType)) {
                String[] shuffledOptions = questionTemplates.shuffleOptions(template.getOptions());
                try {
                    optionsJson = objectMapper.writeValueAsString(Arrays.asList(shuffledOptions));
                } catch (Exception e) {
                    optionsJson = Arrays.toString(template.getOptions());
                }
            } else if ("TRUE_FALSE".equalsIgnoreCase(questionType)) {
                try {
                    optionsJson = objectMapper.writeValueAsString(Arrays.asList("True", "False"));
                } catch (Exception e) {
                    optionsJson = "[\"True\", \"False\"]";
                }
            }

            double confidenceScore = 0.7 + (Math.random() * 0.25);

            AIQuestion aiQuestion = AIQuestion.builder()
                    .questionText(questionText)
                    .questionType(questionType)
                    .difficulty(AIDifficulty.valueOf(difficulty))
                    .bloomLevel(template.getBloomLevel())
                    .options(optionsJson)
                    .correctAnswer(template.getOptions()[template.getCorrectIndex()])
                    .explanation(explanation)
                    .subject(subject)
                    .unit(unit)
                    .topic(topic)
                    .department(department)
                    .isApproved(false)
                    .createdBy(user)
                    .sourcePrompt("Template-based generation for " + subject.getName() + " - " + topicName)
                    .modelVersion(MODEL_VERSION)
                    .confidenceScore(Math.round(confidenceScore * 100.0) / 100.0)
                    .build();

            generated.add(aiQuestionRepository.save(aiQuestion));
        }

        return generated.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<AIQuestionResponse> saveGeneratedQuestions(List<AIQuestionResponse> questions, Long userId) {
        return questions;
    }

    @Override
    @Transactional
    public AIQuestionResponse approveQuestion(Long aiQuestionId, Long facultyId) {
        AIQuestion aiQuestion = aiQuestionRepository.findById(aiQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("AIQuestion", "id", aiQuestionId));

        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", facultyId));

        aiQuestion.setIsApproved(true);
        aiQuestion.setApprovedBy(faculty);
        aiQuestion = aiQuestionRepository.save(aiQuestion);

        return mapToResponse(aiQuestion);
    }

    @Override
    @Transactional
    public AIQuestionResponse rejectQuestion(Long aiQuestionId, Long facultyId) {
        AIQuestion aiQuestion = aiQuestionRepository.findById(aiQuestionId)
                .orElseThrow(() -> new ResourceNotFoundException("AIQuestion", "id", aiQuestionId));

        User faculty = userRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", facultyId));

        aiQuestion.setIsApproved(false);
        aiQuestion.setApprovedBy(faculty);
        aiQuestion = aiQuestionRepository.save(aiQuestion);

        return mapToResponse(aiQuestion);
    }

    @Override
    public PageResponse<AIQuestionResponse> getGeneratedQuestions(Pageable pageable, Long userId, Long subjectId) {
        Page<AIQuestion> page;
        if (subjectId != null) {
            page = aiQuestionRepository.findByCreatedByIdAndSubjectId(userId, subjectId, pageable);
        } else {
            page = aiQuestionRepository.findByCreatedById(userId, pageable);
        }

        List<AIQuestionResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.<AIQuestionResponse>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @Override
    public AIDashboardResponse getAIDashboard() {
        long totalGenerated = aiQuestionRepository.count();
        long approved = aiQuestionRepository.countByIsApproved(true);

        double avgPerformance = studentAnalyticsRepository.findAll().stream()
                .mapToDouble(StudentAnalytics::getAccuracy)
                .average()
                .orElse(0.0);

        long weakTopics = studentAnalyticsRepository.findAll().stream()
                .filter(sa -> sa.getAccuracy() < 50)
                .count();

        long strongTopics = studentAnalyticsRepository.findAll().stream()
                .filter(sa -> sa.getAccuracy() >= 75)
                .count();

        long recommendations = recommendationRepository.count();

        return AIDashboardResponse.builder()
                .aiGeneratedQuestions(totalGenerated)
                .adaptiveExams(approved)
                .studentPerformance(Math.round(avgPerformance * 100.0) / 100.0)
                .weakTopicsCount(weakTopics)
                .strongTopicsCount(strongTopics)
                .recommendationsCount(recommendations)
                .build();
    }

    private AIQuestionResponse mapToResponse(AIQuestion aiQuestion) {
        List<String> parsedOptions = null;
        if (aiQuestion.getOptions() != null) {
            try {
                parsedOptions = objectMapper.readValue(aiQuestion.getOptions(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
            } catch (Exception e) {
                parsedOptions = Arrays.asList(aiQuestion.getOptions().split(","));
            }
        }

        return AIQuestionResponse.builder()
                .id(aiQuestion.getId())
                .questionText(aiQuestion.getQuestionText())
                .questionType(aiQuestion.getQuestionType())
                .difficulty(aiQuestion.getDifficulty() != null ? aiQuestion.getDifficulty().name() : null)
                .bloomLevel(aiQuestion.getBloomLevel())
                .options(parsedOptions)
                .correctAnswer(aiQuestion.getCorrectAnswer())
                .explanation(aiQuestion.getExplanation())
                .subjectName(aiQuestion.getSubject() != null ? aiQuestion.getSubject().getName() : null)
                .unitName(aiQuestion.getUnit() != null ? aiQuestion.getUnit().getName() : null)
                .topicName(aiQuestion.getTopic() != null ? aiQuestion.getTopic().getName() : null)
                .isApproved(aiQuestion.getIsApproved())
                .confidenceScore(aiQuestion.getConfidenceScore())
                .createdAt(aiQuestion.getCreatedAt())
                .build();
    }
}
