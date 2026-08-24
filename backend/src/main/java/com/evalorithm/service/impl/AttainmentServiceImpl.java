package com.evalorithm.service.impl;

import com.evalorithm.dto.request.AttainmentRequest;
import com.evalorithm.dto.response.AttainmentDashboardResponse;
import com.evalorithm.dto.response.AttainmentResponse;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.AttainmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttainmentServiceImpl implements AttainmentService {

    private final AttainmentRepository attainmentRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final COMappingRepository coMappingRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public AttainmentResponse calculateAttainment(AttainmentRequest request) {
        CourseOutcome co = courseOutcomeRepository.findById(request.getCoId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseOutcome", "id", request.getCoId()));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        List<COMapping> mappings = coMappingRepository.findByCoId(co.getId());
        double totalPercentage = 0;
        int count = 0;

        for (COMapping mapping : mappings) {
            Question question = mapping.getQuestion();
            List<ExamQuestion> examQuestions = examQuestionRepository.findAll().stream()
                    .filter(eq -> eq.getQuestion().getId().equals(question.getId()))
                    .toList();

            for (ExamQuestion eq : examQuestions) {
                List<ExamAttempt> attempts = examAttemptRepository.findByExamIdAndIsActiveTrue(eq.getExam().getId());
                for (ExamAttempt attempt : attempts) {
                    List<StudentAnswer> answers = studentAnswerRepository.findByAttemptId(attempt.getId());
                    for (StudentAnswer answer : answers) {
                        if (answer.getExamQuestion().getId().equals(eq.getId())
                                && answer.getMarksAwarded() != null
                                && eq.getMarks() != null && eq.getMarks() > 0) {
                            double percentage = (answer.getMarksAwarded() / eq.getMarks()) * 100.0;
                            totalPercentage += percentage;
                            count++;
                        }
                    }
                }
            }
        }

        double actualAttainment = count > 0 ? totalPercentage / count : 0.0;
        double targetAttainment = request.getTargetAttainment() != null ? request.getTargetAttainment() : 60.0;

        Attainment attainment = Attainment.builder()
                .co(co)
                .subject(subject)
                .semester(semester)
                .academicYear(request.getAcademicYear())
                .targetAttainment(targetAttainment)
                .actualAttainment(actualAttainment)
                .directAttainment(actualAttainment)
                .indirectAttainment(0.0)
                .isAchieved(actualAttainment >= targetAttainment)
                .calculatedAt(LocalDateTime.now())
                .build();

        attainment = attainmentRepository.save(attainment);
        return mapToResponse(attainment);
    }

    @Override
    public AttainmentDashboardResponse getAttainmentDashboard(Long departmentId, String academicYear) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));

        List<Attainment> attainments = attainmentRepository.findByDepartmentIdAndAcademicYear(departmentId, academicYear);
        List<AttainmentResponse> responses = attainments.stream()
                .map(this::mapToResponse)
                .toList();

        double overallTarget = responses.stream()
                .mapToDouble(a -> a.getTargetAttainment() != null ? a.getTargetAttainment() : 0)
                .average().orElse(0.0);
        double overallActual = responses.stream()
                .mapToDouble(a -> a.getActualAttainment() != null ? a.getActualAttainment() : 0)
                .average().orElse(0.0);
        long achievedCount = responses.stream()
                .filter(a -> Boolean.TRUE.equals(a.getIsAchieved()))
                .count();

        return AttainmentDashboardResponse.builder()
                .attainments(responses)
                .overallTarget(overallTarget)
                .overallActual(overallActual)
                .percentageAchieved(responses.isEmpty() ? 0.0 : (achievedCount * 100.0 / responses.size()))
                .departmentName(department.getName())
                .build();
    }

    @Override
    public List<AttainmentResponse> getAttainmentBySubject(Long subjectId, Long semesterId) {
        List<Attainment> attainments = attainmentRepository.findAll().stream()
                .filter(a -> a.getSubject().getId().equals(subjectId) && a.getSemester().getId().equals(semesterId))
                .toList();
        return attainments.stream().map(this::mapToResponse).toList();
    }

    @Override
    public byte[] exportAttainmentReport(Long departmentId, String academicYear, String format) {
        AttainmentDashboardResponse dashboard = getAttainmentDashboard(departmentId, academicYear);
        StringBuilder csv = new StringBuilder();
        csv.append("CO Code,CO Description,Subject,Semester,Academic Year,Target,Actual,Direct,Indirect,Achieved\n");
        for (AttainmentResponse a : dashboard.getAttainments()) {
            csv.append(String.format("%s,%s,%s,%d,%s,%.2f,%.2f,%.2f,%.2f,%s\n",
                    a.getCoCode(), a.getCoDescription(), a.getSubjectName(),
                    a.getSemesterNumber(), a.getAcademicYear(),
                    a.getTargetAttainment(), a.getActualAttainment(),
                    a.getDirectAttainment(), a.getIndirectAttainment(),
                    a.getIsAchieved()));
        }
        return csv.toString().getBytes();
    }

    private AttainmentResponse mapToResponse(Attainment attainment) {
        return AttainmentResponse.builder()
                .id(attainment.getId())
                .coCode(attainment.getCo() != null ? attainment.getCo().getCode() : null)
                .coDescription(attainment.getCo() != null ? attainment.getCo().getDescription() : null)
                .subjectName(attainment.getSubject() != null ? attainment.getSubject().getName() : null)
                .semesterNumber(attainment.getSemester() != null ? attainment.getSemester().getNumber() : null)
                .academicYear(attainment.getAcademicYear())
                .targetAttainment(attainment.getTargetAttainment())
                .actualAttainment(attainment.getActualAttainment())
                .directAttainment(attainment.getDirectAttainment())
                .indirectAttainment(attainment.getIndirectAttainment())
                .isAchieved(attainment.getIsAchieved())
                .build();
    }
}
