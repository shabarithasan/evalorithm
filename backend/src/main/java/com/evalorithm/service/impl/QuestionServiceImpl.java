package com.evalorithm.service.impl;

import com.evalorithm.dto.request.*;
import com.evalorithm.dto.response.*;
import com.evalorithm.entity.*;
import com.evalorithm.enums.ApprovalStatus;
import com.evalorithm.enums.QuestionStatus;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.QuestionService;
import com.evalorithm.util.PaginationUtil;
import com.evalorithm.util.QuestionSpecification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final UnitRepository unitRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final MCQOptionRepository mcqOptionRepository;
    private final ProgrammingQuestionRepository programmingQuestionRepository;
    private final CaseStudyRepository caseStudyRepository;
    private final QuestionVersionRepository questionVersionRepository;
    private final QuestionApprovalRepository questionApprovalRepository;
    private final QuestionStatisticsRepository questionStatisticsRepository;
    private final ObjectMapper objectMapper;

    @Override
    public PageResponse<QuestionResponse> getAllQuestions(QuestionSearchRequest searchRequest, Pageable pageable) {
        Specification<Question> spec = QuestionSpecification.withFilters(searchRequest);
        Page<Question> page = questionRepository.findAll(spec, pageable);
        List<QuestionResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        return mapToResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Question question = Question.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .questionType(request.getQuestionType())
                .difficulty(request.getDifficulty())
                .bloomLevel(request.getBloomLevel())
                .marks(request.getMarks())
                .estimatedTime(request.getEstimatedTime())
                .explanation(request.getExplanation())
                .reference(request.getReference())
                .status(QuestionStatus.DRAFT)
                .courseOutcome(request.getCourseOutcome())
                .programOutcome(request.getProgramOutcome())
                .programSpecificOutcome(request.getProgramSpecificOutcome())
                .createdBy(user)
                .version(1)
                .isArchived(false)
                .build();

        if (request.getCategoryId() != null) {
            QuestionCategory category = questionCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("QuestionCategory", "id", request.getCategoryId()));
            question.setCategory(category);
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            question.setDepartment(department);
        }

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            question.setSemester(semester);
        }

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            question.setSubject(subject);
        }

        if (request.getUnitId() != null) {
            Unit unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", request.getUnitId()));
            question.setUnit(unit);
        }

        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", request.getTopicId()));
            question.setTopic(topic);
        }

        question = questionRepository.save(question);

        if (request.getQuestionType() == com.evalorithm.enums.QuestionType.MCQ && request.getMcqOptions() != null) {
            saveMCQOptions(question, request.getMcqOptions());
        }

        if (request.getQuestionType() == com.evalorithm.enums.QuestionType.PROGRAMMING && request.getProgrammingQuestion() != null) {
            saveProgrammingQuestion(question, request.getProgrammingQuestion());
        }

        if (request.getQuestionType() == com.evalorithm.enums.QuestionType.CASE_STUDY && request.getCaseStudy() != null) {
            saveCaseStudy(question, request.getCaseStudy());
        }

        QuestionStatistics statistics = QuestionStatistics.builder()
                .question(question)
                .viewCount(0)
                .usageCount(0)
                .correctCount(0)
                .wrongCount(0)
                .build();
        questionStatisticsRepository.save(statistics);

        saveVersionSnapshot(question, user, "Initial creation");

        return mapToResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(Long id, QuestionRequest request, Long userId) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        question.setTitle(request.getTitle());
        question.setDescription(request.getDescription());
        question.setQuestionType(request.getQuestionType());
        question.setDifficulty(request.getDifficulty());
        question.setBloomLevel(request.getBloomLevel());
        question.setMarks(request.getMarks());
        question.setEstimatedTime(request.getEstimatedTime());
        question.setExplanation(request.getExplanation());
        question.setReference(request.getReference());
        question.setCourseOutcome(request.getCourseOutcome());
        question.setProgramOutcome(request.getProgramOutcome());
        question.setProgramSpecificOutcome(request.getProgramSpecificOutcome());
        question.setUpdatedBy(user);
        question.setVersion(question.getVersion() + 1);

        if (request.getCategoryId() != null) {
            QuestionCategory category = questionCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("QuestionCategory", "id", request.getCategoryId()));
            question.setCategory(category);
        }

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            question.setDepartment(department);
        }

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            question.setSemester(semester);
        }

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            question.setSubject(subject);
        }

        if (request.getUnitId() != null) {
            Unit unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", request.getUnitId()));
            question.setUnit(unit);
        }

        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", request.getTopicId()));
            question.setTopic(topic);
        }

        question = questionRepository.save(question);

        if (request.getQuestionType() == com.evalorithm.enums.QuestionType.MCQ && request.getMcqOptions() != null) {
            List<MCQOption> existingOptions = mcqOptionRepository.findByQuestionId(id);
            mcqOptionRepository.deleteAll(existingOptions);
            saveMCQOptions(question, request.getMcqOptions());
        }

        if (request.getQuestionType() == com.evalorithm.enums.QuestionType.PROGRAMMING && request.getProgrammingQuestion() != null) {
            programmingQuestionRepository.findByQuestionId(id).ifPresent(programmingQuestionRepository::delete);
            saveProgrammingQuestion(question, request.getProgrammingQuestion());
        }

        if (request.getQuestionType() == com.evalorithm.enums.QuestionType.CASE_STUDY && request.getCaseStudy() != null) {
            caseStudyRepository.findByQuestionId(id).ifPresent(caseStudyRepository::delete);
            saveCaseStudy(question, request.getCaseStudy());
        }

        saveVersionSnapshot(question, user, "Question updated");

        return mapToResponse(question);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", "id", id);
        }
        questionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public QuestionResponse duplicateQuestion(Long id, Long userId) {
        Question original = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Question duplicate = Question.builder()
                .title(original.getTitle() + " (Copy)")
                .description(original.getDescription())
                .questionType(original.getQuestionType())
                .difficulty(original.getDifficulty())
                .bloomLevel(original.getBloomLevel())
                .marks(original.getMarks())
                .estimatedTime(original.getEstimatedTime())
                .explanation(original.getExplanation())
                .reference(original.getReference())
                .status(QuestionStatus.DRAFT)
                .category(original.getCategory())
                .department(original.getDepartment())
                .semester(original.getSemester())
                .subject(original.getSubject())
                .unit(original.getUnit())
                .topic(original.getTopic())
                .courseOutcome(original.getCourseOutcome())
                .programOutcome(original.getProgramOutcome())
                .programSpecificOutcome(original.getProgramSpecificOutcome())
                .createdBy(user)
                .version(1)
                .isArchived(false)
                .build();

        duplicate = questionRepository.save(duplicate);

        if (original.getQuestionType() == com.evalorithm.enums.QuestionType.MCQ && original.getMcqOptions() != null) {
            List<MCQOptionRequest> options = original.getMcqOptions().stream()
                    .map(opt -> new MCQOptionRequest(opt.getOptionLabel(), opt.getOptionText(), opt.getIsCorrect(), opt.getExplanation()))
                    .toList();
            saveMCQOptions(duplicate, options);
        }

        if (original.getQuestionType() == com.evalorithm.enums.QuestionType.PROGRAMMING && original.getProgrammingQuestion() != null) {
            ProgrammingQuestion origPq = original.getProgrammingQuestion();
            ProgrammingQuestionRequest pqRequest = new ProgrammingQuestionRequest(
                    origPq.getProblemStatement(), origPq.getInputFormat(), origPq.getOutputFormat(),
                    origPq.getConstraints(), origPq.getSampleInput(), origPq.getSampleOutput(),
                    origPq.getTestCases(), origPq.getStarterCode(), origPq.getSolutionCode(),
                    origPq.getProgrammingLanguage()
            );
            saveProgrammingQuestion(duplicate, pqRequest);
        }

        if (original.getQuestionType() == com.evalorithm.enums.QuestionType.CASE_STUDY && original.getCaseStudy() != null) {
            CaseStudy origCs = original.getCaseStudy();
            CaseStudyRequest csRequest = new CaseStudyRequest(origCs.getScenario(), origCs.getSubQuestions());
            saveCaseStudy(duplicate, csRequest);
        }

        QuestionStatistics statistics = QuestionStatistics.builder()
                .question(duplicate)
                .viewCount(0)
                .usageCount(0)
                .correctCount(0)
                .wrongCount(0)
                .build();
        questionStatisticsRepository.save(statistics);

        saveVersionSnapshot(duplicate, user, "Duplicated from question #" + id);

        return mapToResponse(duplicate);
    }

    @Override
    @Transactional
    public QuestionResponse archiveQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        question.setIsArchived(true);
        question = questionRepository.save(question);
        return mapToResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse restoreQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        question.setIsArchived(false);
        question = questionRepository.save(question);
        return mapToResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse submitForReview(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        if (question.getStatus() != QuestionStatus.DRAFT && question.getStatus() != QuestionStatus.REJECTED) {
            throw new BadRequestException("Only DRAFT or REJECTED questions can be submitted for review");
        }

        question.setStatus(QuestionStatus.PENDING_REVIEW);
        question = questionRepository.save(question);
        return mapToResponse(question);
    }

    @Override
    @Transactional
    public QuestionResponse approveQuestion(Long id, Long approverId, QuestionApprovalRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        User approver = userRepository.findById(approverId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", approverId));

        QuestionApproval approval = QuestionApproval.builder()
                .question(question)
                .approver(approver)
                .status(request.getStatus())
                .comments(request.getComments())
                .approvedAt(request.getStatus() == ApprovalStatus.APPROVED ? LocalDateTime.now() : null)
                .build();
        questionApprovalRepository.save(approval);

        if (request.getStatus() == ApprovalStatus.APPROVED) {
            question.setStatus(QuestionStatus.APPROVED);
        } else if (request.getStatus() == ApprovalStatus.REJECTED) {
            question.setStatus(QuestionStatus.REJECTED);
        }

        question.setUpdatedBy(approver);
        question = questionRepository.save(question);
        return mapToResponse(question);
    }

    @Override
    public List<QuestionVersionResponse> getQuestionVersions(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question", "id", questionId);
        }
        List<QuestionVersion> versions = questionVersionRepository.findByQuestionIdOrderByVersionNumberDesc(questionId);
        return versions.stream().map(this::mapToVersionResponse).toList();
    }

    @Override
    public QuestionDashboardResponse getQuestionDashboard() {
        long total = questionRepository.count();
        long approved = questionRepository.countByStatus(QuestionStatus.APPROVED);
        long pending = questionRepository.countByStatus(QuestionStatus.PENDING_REVIEW);
        long rejected = questionRepository.countByStatus(QuestionStatus.REJECTED);
        long recent = questionRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(7));

        return QuestionDashboardResponse.builder()
                .totalQuestions(total)
                .approvedQuestions(approved)
                .pendingQuestions(pending)
                .rejectedQuestions(rejected)
                .recentlyAdded(recent)
                .build();
    }

    private void saveMCQOptions(Question question, List<MCQOptionRequest> options) {
        if (options == null) return;
        for (MCQOptionRequest opt : options) {
            MCQOption mcqOption = MCQOption.builder()
                    .question(question)
                    .optionLabel(opt.getOptionLabel())
                    .optionText(opt.getOptionText())
                    .isCorrect(opt.getIsCorrect() != null ? opt.getIsCorrect() : false)
                    .explanation(opt.getExplanation())
                    .build();
            mcqOptionRepository.save(mcqOption);
        }
    }

    private void saveProgrammingQuestion(Question question, ProgrammingQuestionRequest request) {
        if (request == null) return;
        ProgrammingQuestion pq = ProgrammingQuestion.builder()
                .question(question)
                .problemStatement(request.getProblemStatement())
                .inputFormat(request.getInputFormat())
                .outputFormat(request.getOutputFormat())
                .constraints(request.getConstraints())
                .sampleInput(request.getSampleInput())
                .sampleOutput(request.getSampleOutput())
                .testCases(request.getTestCases())
                .starterCode(request.getStarterCode())
                .solutionCode(request.getSolutionCode())
                .programmingLanguage(request.getProgrammingLanguage())
                .build();
        programmingQuestionRepository.save(pq);
    }

    private void saveCaseStudy(Question question, CaseStudyRequest request) {
        if (request == null) return;
        CaseStudy cs = CaseStudy.builder()
                .question(question)
                .scenario(request.getScenario())
                .subQuestions(request.getSubQuestions())
                .build();
        caseStudyRepository.save(cs);
    }

    private void saveVersionSnapshot(Question question, User user, String changeDescription) {
        try {
            String snapshot = objectMapper.writeValueAsString(question);
            QuestionVersion version = QuestionVersion.builder()
                    .question(question)
                    .versionNumber(question.getVersion())
                    .updatedBy(user)
                    .changeDescription(changeDescription)
                    .snapshot(snapshot)
                    .build();
            questionVersionRepository.save(version);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Failed to create version snapshot");
        }
    }

    private QuestionResponse mapToResponse(Question question) {
        QuestionResponse.QuestionResponseBuilder builder = QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .description(question.getDescription())
                .questionType(question.getQuestionType())
                .difficulty(question.getDifficulty())
                .bloomLevel(question.getBloomLevel())
                .marks(question.getMarks())
                .estimatedTime(question.getEstimatedTime())
                .explanation(question.getExplanation())
                .reference(question.getReference())
                .status(question.getStatus())
                .courseOutcome(question.getCourseOutcome())
                .programOutcome(question.getProgramOutcome())
                .programSpecificOutcome(question.getProgramSpecificOutcome())
                .version(question.getVersion())
                .isArchived(question.getIsArchived())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt());

        if (question.getCategory() != null) {
            builder.categoryId(question.getCategory().getId());
            builder.categoryName(question.getCategory().getCategoryName());
        }

        if (question.getDepartment() != null) {
            builder.departmentId(question.getDepartment().getId());
            builder.departmentName(question.getDepartment().getName());
        }

        if (question.getSemester() != null) {
            builder.semesterId(question.getSemester().getId());
            builder.semesterNumber(question.getSemester().getNumber());
        }

        if (question.getSubject() != null) {
            builder.subjectId(question.getSubject().getId());
            builder.subjectName(question.getSubject().getName());
        }

        if (question.getUnit() != null) {
            builder.unitId(question.getUnit().getId());
            builder.unitNumber(question.getUnit().getNumber());
            builder.unitName(question.getUnit().getName());
        }

        if (question.getTopic() != null) {
            builder.topicId(question.getTopic().getId());
            builder.topicName(question.getTopic().getName());
        }

        if (question.getCreatedBy() != null) {
            builder.createdById(question.getCreatedBy().getId());
            String creatorName = (question.getCreatedBy().getFirstName() != null ? question.getCreatedBy().getFirstName() : "") +
                    (question.getCreatedBy().getLastName() != null ? " " + question.getCreatedBy().getLastName() : "");
            builder.createdByName(creatorName.trim());
        }

        if (question.getUpdatedBy() != null) {
            builder.updatedById(question.getUpdatedBy().getId());
            String updaterName = (question.getUpdatedBy().getFirstName() != null ? question.getUpdatedBy().getFirstName() : "") +
                    (question.getUpdatedBy().getLastName() != null ? " " + question.getUpdatedBy().getLastName() : "");
            builder.updatedByName(updaterName.trim());
        }

        if (question.getMcqOptions() != null && !question.getMcqOptions().isEmpty()) {
            List<MCQOptionResponse> optionResponses = question.getMcqOptions().stream()
                    .map(opt -> MCQOptionResponse.builder()
                            .id(opt.getId())
                            .optionLabel(opt.getOptionLabel())
                            .optionText(opt.getOptionText())
                            .isCorrect(opt.getIsCorrect())
                            .explanation(opt.getExplanation())
                            .build())
                    .toList();
            builder.mcqOptions(optionResponses);
        }

        if (question.getProgrammingQuestion() != null) {
            ProgrammingQuestion pq = question.getProgrammingQuestion();
            builder.programmingQuestion(ProgrammingQuestionResponse.builder()
                    .id(pq.getId())
                    .problemStatement(pq.getProblemStatement())
                    .inputFormat(pq.getInputFormat())
                    .outputFormat(pq.getOutputFormat())
                    .constraints(pq.getConstraints())
                    .sampleInput(pq.getSampleInput())
                    .sampleOutput(pq.getSampleOutput())
                    .testCases(pq.getTestCases())
                    .starterCode(pq.getStarterCode())
                    .solutionCode(pq.getSolutionCode())
                    .programmingLanguage(pq.getProgrammingLanguage())
                    .build());
        }

        if (question.getCaseStudy() != null) {
            CaseStudy cs = question.getCaseStudy();
            builder.caseStudy(CaseStudyResponse.builder()
                    .id(cs.getId())
                    .scenario(cs.getScenario())
                    .subQuestions(cs.getSubQuestions())
                    .build());
        }

        if (question.getStatistics() != null) {
            QuestionStatistics stats = question.getStatistics();
            int totalAttempts = stats.getCorrectCount() + stats.getWrongCount();
            builder.statistics(QuestionStatisticsResponse.builder()
                    .id(stats.getId())
                    .viewCount(stats.getViewCount())
                    .usageCount(stats.getUsageCount())
                    .correctCount(stats.getCorrectCount())
                    .wrongCount(stats.getWrongCount())
                    .correctPercentage(totalAttempts > 0 ? (double) stats.getCorrectCount() / totalAttempts * 100 : 0.0)
                    .wrongPercentage(totalAttempts > 0 ? (double) stats.getWrongCount() / totalAttempts * 100 : 0.0)
                    .lastUsedAt(stats.getLastUsedAt())
                    .build());
        }

        if (question.getMedia() != null && !question.getMedia().isEmpty()) {
            List<QuestionMediaResponse> mediaResponses = question.getMedia().stream()
                    .map(m -> QuestionMediaResponse.builder()
                            .id(m.getId())
                            .fileName(m.getFileName())
                            .fileUrl(m.getFileUrl())
                            .fileType(m.getFileType())
                            .fileSize(m.getFileSize())
                            .uploadedAt(m.getUploadedAt())
                            .build())
                    .toList();
            builder.media(mediaResponses);
        }

        return builder.build();
    }

    private QuestionVersionResponse mapToVersionResponse(QuestionVersion version) {
        String updaterName = "";
        if (version.getUpdatedBy() != null) {
            updaterName = (version.getUpdatedBy().getFirstName() != null ? version.getUpdatedBy().getFirstName() : "") +
                    (version.getUpdatedBy().getLastName() != null ? " " + version.getUpdatedBy().getLastName() : "");
        }
        return QuestionVersionResponse.builder()
                .id(version.getId())
                .versionNumber(version.getVersionNumber())
                .updatedByName(updaterName.trim())
                .changeDescription(version.getChangeDescription())
                .createdAt(version.getCreatedAt())
                .build();
    }
}
