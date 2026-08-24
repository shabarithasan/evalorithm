package com.evalorithm.service.impl;

import com.evalorithm.dto.request.ReportGenerateRequest;
import com.evalorithm.entity.*;
import com.evalorithm.repository.*;
import com.evalorithm.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final StudentProfileRepository studentProfileRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionRepository questionRepository;
    private final FacultyProfileRepository facultyProfileRepository;

    @Override
    public byte[] generateReport(ReportGenerateRequest request) {
        String reportType = request.getReportType() != null ? request.getReportType() : "ANALYTICS";
        StringBuilder csv = new StringBuilder();

        switch (reportType) {
            case "STUDENT" -> generateStudentReport(csv, request);
            case "FACULTY" -> generateFacultyReport(csv, request);
            case "DEPARTMENT" -> generateDepartmentReport(csv, request);
            case "SUBJECT" -> generateSubjectReport(csv, request);
            case "QUESTION_BANK" -> generateQuestionBankReport(csv, request);
            case "EXAM" -> generateExamReport(csv, request);
            default -> generateAnalyticsReport(csv, request);
        }

        log.info("Generated {} report, format: {}", reportType, request.getFormat());
        return csv.toString().getBytes();
    }

    private void generateStudentReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Student ID,Register Number,Name,Department,Semester,Total Exams,Average Score\n");
        List<StudentProfile> students = studentProfileRepository.findAll();
        for (StudentProfile s : students) {
            List<ExamResult> results = examResultRepository.findByStudentProfileId(s.getId());
            double avg = results.stream().mapToDouble(r -> r.getPercentage() != null ? r.getPercentage() : 0).average().orElse(0);
            csv.append(String.format("%d,%s,%s %s,%s,%d,%d,%.2f\n",
                    s.getId(), s.getRegisterNumber(),
                    s.getUser().getFirstName(), s.getUser().getLastName(),
                    s.getDepartment() != null ? s.getDepartment().getName() : "",
                    s.getSemester() != null ? s.getSemester().getNumber() : 0,
                    results.size(), avg));
        }
    }

    private void generateFacultyReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Faculty ID,Name,Department,Total Exams Created\n");
        List<FacultyProfile> faculties = facultyProfileRepository.findAll();
        for (FacultyProfile f : faculties) {
            long examCount = examRepository.findByCreatedById(f.getUser().getId()).size();
            csv.append(String.format("%d,%s %s,%s,%d\n",
                    f.getId(),
                    f.getUser().getFirstName(), f.getUser().getLastName(),
                    f.getDepartment() != null ? f.getDepartment().getName() : "",
                    examCount));
        }
    }

    private void generateDepartmentReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Department ID,Code,Name,Total Students,Total Exams,Total Subjects\n");
        List<Department> departments = departmentRepository.findAll();
        for (Department d : departments) {
            List<StudentProfile> students = studentProfileRepository.findByDepartmentId(d.getId());
            List<Subject> subjects = subjectRepository.findByDepartmentId(d.getId());
            csv.append(String.format("%d,%s,%s,%d,%d,%d\n",
                    d.getId(), d.getCode(), d.getName(),
                    students.size(), 0, subjects.size()));
        }
    }

    private void generateSubjectReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Subject ID,Code,Name,Department,Semester,Credits\n");
        List<Subject> subjects = subjectRepository.findAll();
        for (Subject s : subjects) {
            csv.append(String.format("%d,%s,%s,%s,%d,%d\n",
                    s.getId(), s.getCode(), s.getName(),
                    s.getDepartment() != null ? s.getDepartment().getName() : "",
                    s.getSemester() != null ? s.getSemester().getNumber() : 0,
                    s.getCredits()));
        }
    }

    private void generateQuestionBankReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Question ID,Title,Type,Difficulty,Bloom Level,Status,Subject\n");
        List<Question> questions = questionRepository.findAll();
        for (Question q : questions) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                    q.getId(),
                    q.getTitle() != null ? q.getTitle().replace(",", ";") : "",
                    q.getQuestionType(),
                    q.getDifficulty(),
                    q.getBloomLevel(),
                    q.getStatus(),
                    q.getSubject() != null ? q.getSubject().getName() : ""));
        }
    }

    private void generateExamReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Exam ID,Title,Type,Status,Total Marks,Passing Marks,Start Date,End Date\n");
        List<Exam> exams = examRepository.findAll();
        for (Exam e : exams) {
            csv.append(String.format("%d,%s,%s,%s,%d,%d,%s,%s\n",
                    e.getId(),
                    e.getTitle().replace(",", ";"),
                    e.getExamType(),
                    e.getStatus(),
                    e.getTotalMarks(),
                    e.getPassingMarks(),
                    e.getStartDate() != null ? e.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "",
                    e.getEndDate() != null ? e.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : ""));
        }
    }

    private void generateAnalyticsReport(StringBuilder csv, ReportGenerateRequest request) {
        csv.append("Metric,Value\n");
        csv.append(String.format("Total Students,%d\n", studentProfileRepository.count()));
        csv.append(String.format("Total Departments,%d\n", departmentRepository.count()));
        csv.append(String.format("Total Subjects,%d\n", subjectRepository.count()));
        csv.append(String.format("Total Questions,%d\n", questionRepository.count()));
        csv.append(String.format("Total Exams,%d\n", examRepository.count()));
        csv.append(String.format("Report Generated,%s\n", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
    }
}
