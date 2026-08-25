package com.evalorithm.service.impl;

import com.evalorithm.dto.request.ExamQuestionRequest;
import com.evalorithm.dto.request.ExamRequest;
import com.evalorithm.dto.response.*;
import com.evalorithm.entity.*;
import com.evalorithm.enums.ExamStatus;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamStudentRepository examStudentRepository;
    private final QuestionRepository questionRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> getAllExams(Pageable pageable, String search, String status,
                                                  String examType, Long departmentId) {
        ExamStatus examStatus = null;
        com.evalorithm.enums.ExamType type = null;
        if (status != null) {
            examStatus = ExamStatus.valueOf(status);
        }
        if (examType != null) {
            type = com.evalorithm.enums.ExamType.valueOf(examType);
        }
        Page<Exam> page = examRepository.findAll(pageable);
        List<ExamResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamDetailResponse getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        ExamDetailResponse detailResponse = (ExamDetailResponse) mapToResponse(exam);

        List<ExamQuestionResponse> questionResponses = examQuestionRepository.findByExamIdOrderByOrderNumberAsc(id)
                .stream()
                .filter(ExamQuestion::getIsActive)
                .map(this::mapToExamQuestionResponse)
                .toList();
        detailResponse.setExamQuestions(questionResponses);

        List<ExamStudentResponse> studentResponses = examStudentRepository.findByExamId(id)
                .stream()
                .map(this::mapToExamStudentResponse)
                .toList();
        detailResponse.setAssignedStudents(studentResponses);

        return detailResponse;
    }

    @Override
    @Transactional
    public ExamResponse createExam(ExamRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Exam exam = Exam.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .examType(request.getExamType())
                .status(ExamStatus.DRAFT)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .durationMinutes(request.getDurationMinutes())
                .totalMarks(request.getTotalMarks())
                .passingMarks(request.getPassingMarks())
                .maxAttempts(request.getMaxAttempts() != null ? request.getMaxAttempts() : 1)
                .negativeMarksEnabled(request.getNegativeMarksEnabled() != null ? request.getNegativeMarksEnabled() : false)
                .negativeMarksValue(request.getNegativeMarksValue() != null ? request.getNegativeMarksValue() : 0.0)
                .randomizeQuestions(request.getRandomizeQuestions() != null ? request.getRandomizeQuestions() : false)
                .randomizeOptions(request.getRandomizeOptions() != null ? request.getRandomizeOptions() : false)
                .showResultsImmediately(request.getShowResultsImmediately() != null ? request.getShowResultsImmediately() : false)
                .autoSubmit(request.getAutoSubmit() != null ? request.getAutoSubmit() : true)
                .fullscreenRequired(request.getFullscreenRequired() != null ? request.getFullscreenRequired() : true)
                .preventTabSwitch(request.getPreventTabSwitch() != null ? request.getPreventTabSwitch() : true)
                .createdBy(user)
                .build();

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            exam.setDepartment(department);
        }

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            exam.setSemester(semester);
        }

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            exam.setSubject(subject);
        }

        exam = examRepository.save(exam);

        if (request.getExamQuestions() != null && !request.getExamQuestions().isEmpty()) {
            addQuestionsToExam(exam.getId(), request.getExamQuestions());
        }

        if (request.getAssignStudentIds() != null && !request.getAssignStudentIds().isEmpty()) {
            assignStudents(exam.getId(), request.getAssignStudentIds());
        }

        return mapToResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        if (exam.getStatus() == ExamStatus.ACTIVE || exam.getStatus() == ExamStatus.COMPLETED) {
            throw new BadRequestException("Cannot update an active or completed exam");
        }

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setExamType(request.getExamType());
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setDurationMinutes(request.getDurationMinutes());
        exam.setTotalMarks(request.getTotalMarks());
        exam.setPassingMarks(request.getPassingMarks());

        if (request.getMaxAttempts() != null) exam.setMaxAttempts(request.getMaxAttempts());
        if (request.getNegativeMarksEnabled() != null) exam.setNegativeMarksEnabled(request.getNegativeMarksEnabled());
        if (request.getNegativeMarksValue() != null) exam.setNegativeMarksValue(request.getNegativeMarksValue());
        if (request.getRandomizeQuestions() != null) exam.setRandomizeQuestions(request.getRandomizeQuestions());
        if (request.getRandomizeOptions() != null) exam.setRandomizeOptions(request.getRandomizeOptions());
        if (request.getShowResultsImmediately() != null) exam.setShowResultsImmediately(request.getShowResultsImmediately());
        if (request.getAutoSubmit() != null) exam.setAutoSubmit(request.getAutoSubmit());
        if (request.getFullscreenRequired() != null) exam.setFullscreenRequired(request.getFullscreenRequired());
        if (request.getPreventTabSwitch() != null) exam.setPreventTabSwitch(request.getPreventTabSwitch());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            exam.setDepartment(department);
        }

        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            exam.setSemester(semester);
        }

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            exam.setSubject(subject);
        }

        exam = examRepository.save(exam);
        return mapToResponse(exam);
    }

    @Override
    @Transactional
    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        if (exam.getStatus() == ExamStatus.ACTIVE) {
            throw new BadRequestException("Cannot delete an active exam");
        }
        examRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ExamResponse cloneExam(Long id, Long userId) {
        Exam original = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Exam clone = Exam.builder()
                .title(original.getTitle() + " (Clone)")
                .description(original.getDescription())
                .examType(original.getExamType())
                .status(ExamStatus.DRAFT)
                .startDate(original.getStartDate())
                .endDate(original.getEndDate())
                .durationMinutes(original.getDurationMinutes())
                .totalMarks(original.getTotalMarks())
                .passingMarks(original.getPassingMarks())
                .maxAttempts(original.getMaxAttempts())
                .negativeMarksEnabled(original.getNegativeMarksEnabled())
                .negativeMarksValue(original.getNegativeMarksValue())
                .randomizeQuestions(original.getRandomizeQuestions())
                .randomizeOptions(original.getRandomizeOptions())
                .showResultsImmediately(original.getShowResultsImmediately())
                .autoSubmit(original.getAutoSubmit())
                .fullscreenRequired(original.getFullscreenRequired())
                .preventTabSwitch(original.getPreventTabSwitch())
                .department(original.getDepartment())
                .semester(original.getSemester())
                .subject(original.getSubject())
                .createdBy(user)
                .build();

        clone = examRepository.save(clone);

        List<ExamQuestion> originalQuestions = examQuestionRepository.findByExamId(id);
        for (ExamQuestion eq : originalQuestions) {
            ExamQuestion clonedQuestion = ExamQuestion.builder()
                    .exam(clone)
                    .question(eq.getQuestion())
                    .marks(eq.getMarks())
                    .orderNumber(eq.getOrderNumber())
                    .isActive(eq.getIsActive())
                    .build();
            examQuestionRepository.save(clonedQuestion);
        }

        return mapToResponse(clone);
    }

    @Override
    @Transactional
    public ExamResponse publishExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        if (exam.getStatus() != ExamStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT exams can be published");
        }

        if (exam.getExamQuestions() == null || exam.getExamQuestions().isEmpty()) {
            throw new BadRequestException("Cannot publish exam without questions");
        }

        exam.setStatus(ExamStatus.PUBLISHED);
        exam = examRepository.save(exam);
        return mapToResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse archiveExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        exam.setStatus(ExamStatus.ARCHIVED);
        exam = examRepository.save(exam);
        return mapToResponse(exam);
    }

    @Override
    @Transactional
    public ExamResponse cancelExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
        exam.setStatus(ExamStatus.CANCELLED);
        exam = examRepository.save(exam);
        return mapToResponse(exam);
    }

    @Override
    @Transactional
    public void addQuestionsToExam(Long examId, List<ExamQuestionRequest> questions) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        int maxOrder = examQuestionRepository.findByExamId(examId).stream()
                .mapToInt(eq -> eq.getOrderNumber() != null ? eq.getOrderNumber() : 0)
                .max().orElse(0);

        for (ExamQuestionRequest request : questions) {
            Question question = questionRepository.findById(request.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Question", "id", request.getQuestionId()));

            ExamQuestion examQuestion = ExamQuestion.builder()
                    .exam(exam)
                    .question(question)
                    .marks(request.getMarks())
                    .orderNumber(request.getOrderNumber() != null ? request.getOrderNumber() : ++maxOrder)
                    .isActive(true)
                    .build();
            examQuestionRepository.save(examQuestion);
        }
    }

    @Override
    @Transactional
    public void removeQuestionFromExam(Long examId, Long questionId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamId(examId);
        ExamQuestion toRemove = examQuestions.stream()
                .filter(eq -> eq.getQuestion().getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ExamQuestion", "questionId", questionId));
        examQuestionRepository.delete(toRemove);
    }

    @Override
    @Transactional
    public void assignStudents(Long examId, List<Long> studentIds) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        for (Long studentId : studentIds) {
            StudentProfile student = studentProfileRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "id", studentId));

            if (!examStudentRepository.existsByExamIdAndStudentProfileId(examId, studentId)) {
                ExamStudent examStudent = ExamStudent.builder()
                        .exam(exam)
                        .studentProfile(student)
                        .assignedAt(LocalDateTime.now())
                        .isEligible(true)
                        .build();
                examStudentRepository.save(examStudent);
            }
        }
    }

    @Override
    @Transactional
    public void unassignStudents(Long examId, List<Long> studentIds) {
        examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        for (Long studentId : studentIds) {
            ExamStudent examStudent = examStudentRepository.findByExamIdAndStudentProfileId(examId, studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("ExamStudent", "studentId", studentId));
            examStudentRepository.delete(examStudent);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ExamDashboardResponse getExamDashboard() {
        long total = examRepository.count();
        long active = examRepository.countByStatus(ExamStatus.ACTIVE);
        long draft = examRepository.countByStatus(ExamStatus.DRAFT);
        long completed = examRepository.countByStatus(ExamStatus.COMPLETED);
        long published = examRepository.countByStatus(ExamStatus.PUBLISHED);
        long scheduled = examRepository.findByStatus(ExamStatus.PUBLISHED).stream()
                .filter(e -> e.getStartDate().isAfter(LocalDateTime.now()))
                .count();

        return ExamDashboardResponse.builder()
                .totalExams(total)
                .activeExams(active)
                .scheduledExams(scheduled)
                .completedExams(completed)
                .draftExams(draft)
                .build();
    }

    private ExamResponse mapToResponse(Exam exam) {
        ExamDetailResponse.ExamDetailResponseBuilder builder = ExamDetailResponse.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .examType(exam.getExamType())
                .status(exam.getStatus())
                .startDate(exam.getStartDate())
                .endDate(exam.getEndDate())
                .durationMinutes(exam.getDurationMinutes())
                .totalMarks(exam.getTotalMarks())
                .passingMarks(exam.getPassingMarks())
                .maxAttempts(exam.getMaxAttempts())
                .negativeMarksEnabled(exam.getNegativeMarksEnabled())
                .negativeMarksValue(exam.getNegativeMarksValue())
                .randomizeQuestions(exam.getRandomizeQuestions())
                .randomizeOptions(exam.getRandomizeOptions())
                .showResultsImmediately(exam.getShowResultsImmediately())
                .autoSubmit(exam.getAutoSubmit())
                .questionCount(exam.getExamQuestions() != null ? exam.getExamQuestions().size() : 0)
                .studentCount(exam.getExamStudents() != null ? exam.getExamStudents().size() : 0)
                .createdAt(exam.getCreatedAt());

        if (exam.getDepartment() != null) {
            builder.departmentId(exam.getDepartment().getId());
            builder.departmentName(exam.getDepartment().getName());
        }

        if (exam.getSemester() != null) {
            builder.semesterId(exam.getSemester().getId());
            builder.semesterNumber(exam.getSemester().getNumber());
        }

        if (exam.getSubject() != null) {
            builder.subjectId(exam.getSubject().getId());
            builder.subjectName(exam.getSubject().getName());
        }

        if (exam.getCreatedBy() != null) {
            builder.createdById(exam.getCreatedBy().getId());
            String creatorName = (exam.getCreatedBy().getFirstName() != null ? exam.getCreatedBy().getFirstName() : "") +
                    (exam.getCreatedBy().getLastName() != null ? " " + exam.getCreatedBy().getLastName() : "");
            builder.createdByName(creatorName.trim());
        }

        return builder.build();
    }

    private ExamQuestionResponse mapToExamQuestionResponse(ExamQuestion eq) {
        Question question = eq.getQuestion();
        ExamQuestionResponse response = ExamQuestionResponse.builder()
                .id(eq.getId())
                .questionId(question.getId())
                .questionTitle(question.getTitle())
                .questionType(question.getQuestionType())
                .marks(eq.getMarks())
                .orderNumber(eq.getOrderNumber())
                .isActive(eq.getIsActive())
                .questionDescription(question.getDescription())
                .difficulty(question.getDifficulty() != null ? question.getDifficulty().name() : null)
                .build();

        if (question.getQuestionType() == com.evalorithm.enums.QuestionType.MCQ && question.getMcqOptions() != null) {
            List<MCQOptionResponse> options = question.getMcqOptions().stream()
                    .map(opt -> MCQOptionResponse.builder()
                            .id(opt.getId())
                            .optionLabel(opt.getOptionLabel())
                            .optionText(opt.getOptionText())
                            .isCorrect(opt.getIsCorrect())
                            .explanation(opt.getExplanation())
                            .build())
                    .toList();
            response.setOptions(options);
        }

        return response;
    }

    private ExamStudentResponse mapToExamStudentResponse(ExamStudent es) {
        StudentProfile sp = es.getStudentProfile();
        User user = sp.getUser();
        String studentName = (user.getFirstName() != null ? user.getFirstName() : "") +
                (user.getLastName() != null ? " " + user.getLastName() : "");

        return ExamStudentResponse.builder()
                .studentProfileId(sp.getId())
                .userId(user.getId())
                .studentName(studentName.trim())
                .registerNumber(sp.getRegisterNumber())
                .email(user.getEmail())
                .isEligible(es.getIsEligible())
                .build();
    }
}
