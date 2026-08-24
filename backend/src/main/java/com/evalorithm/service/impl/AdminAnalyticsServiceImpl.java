package com.evalorithm.service.impl;

import com.evalorithm.dto.response.AdminAnalyticsResponse;
import com.evalorithm.entity.*;
import com.evalorithm.repository.*;
import com.evalorithm.service.AdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final StudentProfileRepository studentProfileRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;
    private final ExamResultRepository examResultRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Override
    public Map<String, Object> getOverallStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentProfileRepository.count());
        stats.put("totalFaculty", facultyProfileRepository.count());
        stats.put("totalSubjects", subjectRepository.count());
        stats.put("totalExams", examRepository.count());

        List<ExamResult> allResults = examResultRepository.findAll();
        double avgScore = allResults.stream()
                .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                .average()
                .orElse(0.0);
        stats.put("averageScore", Math.round(avgScore * 100.0) / 100.0);

        long passed = allResults.stream()
                .filter(er -> Boolean.TRUE.equals(er.getIsPassed()))
                .count();
        stats.put("overallPassRate", allResults.isEmpty() ? 0.0 :
                Math.round((double) passed / allResults.size() * 10000.0) / 100.0);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getDepartmentPerformance() {
        List<Department> departments = departmentRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Department dept : departments) {
            List<StudentProfile> deptStudents = studentProfileRepository.findByDepartmentId(dept.getId());

            double avgScore = deptStudents.stream()
                    .flatMap(sp -> examResultRepository.findByStudentProfileId(sp.getId()).stream())
                    .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                    .average()
                    .orElse(0.0);

            long passed = deptStudents.stream()
                    .flatMap(sp -> examResultRepository.findByStudentProfileId(sp.getId()).stream())
                    .filter(er -> Boolean.TRUE.equals(er.getIsPassed()))
                    .count();

            long totalAttempts = deptStudents.stream()
                    .flatMap(sp -> examResultRepository.findByStudentProfileId(sp.getId()).stream())
                    .count();

            Map<String, Object> deptPerf = new HashMap<>();
            deptPerf.put("departmentId", dept.getId());
            deptPerf.put("departmentName", dept.getName());
            deptPerf.put("totalStudents", deptStudents.size());
            deptPerf.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
            deptPerf.put("passRate", totalAttempts > 0 ? Math.round((double) passed / totalAttempts * 10000.0) / 100.0 : 0.0);
            result.add(deptPerf);
        }
        return result;
    }

    @Override
    public List<Map<String, Object>> getStudentGrowth() {
        List<Map<String, Object>> growth = new ArrayList<>();
        YearMonth now = YearMonth.now();

        for (int i = 11; i >= 0; i--) {
            YearMonth month = now.minusMonths(i);
            LocalDateTime startOfMonth = month.atDay(1).atStartOfDay();
            LocalDateTime endOfMonth = month.atEndOfMonth().atTime(23, 59, 59);

            long count = userRepository.findAll().stream()
                    .filter(u -> u.getCreatedAt() != null
                            && u.getCreatedAt().isAfter(startOfMonth)
                            && u.getCreatedAt().isBefore(endOfMonth))
                    .filter(u -> u.getRole() != null && u.getRole().name().contains("STUDENT"))
                    .count();

            Map<String, Object> point = new HashMap<>();
            point.put("month", month.toString());
            point.put("count", count);
            growth.add(point);
        }
        return growth;
    }

    @Override
    public List<Map<String, Object>> getTopPerformersGlobal(int limit) {
        List<StudentProfile> allStudents = studentProfileRepository.findAll();

        return allStudents.stream()
                .map(sp -> {
                    double avgScore = examResultRepository.findByStudentProfileId(sp.getId()).stream()
                            .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                            .average()
                            .orElse(0.0);

                    String name = "";
                    if (sp.getUser() != null) {
                        name = (sp.getUser().getFirstName() != null ? sp.getUser().getFirstName() : "") +
                                (sp.getUser().getLastName() != null ? " " + sp.getUser().getLastName() : "");
                    }

                    Map<String, Object> performer = new HashMap<>();
                    performer.put("studentId", sp.getUser() != null ? sp.getUser().getId() : null);
                    performer.put("studentName", name.trim());
                    performer.put("score", Math.round(avgScore * 100.0) / 100.0);
                    performer.put("departmentName", sp.getDepartment() != null ? sp.getDepartment().getName() : "Unknown");
                    return performer;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("score"), (Double) a.get("score")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getLowPerformersGlobal(int limit) {
        List<StudentProfile> allStudents = studentProfileRepository.findAll();

        return allStudents.stream()
                .map(sp -> {
                    double avgScore = examResultRepository.findByStudentProfileId(sp.getId()).stream()
                            .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                            .average()
                            .orElse(0.0);

                    String name = "";
                    if (sp.getUser() != null) {
                        name = (sp.getUser().getFirstName() != null ? sp.getUser().getFirstName() : "") +
                                (sp.getUser().getLastName() != null ? " " + sp.getUser().getLastName() : "");
                    }

                    Map<String, Object> performer = new HashMap<>();
                    performer.put("studentId", sp.getUser() != null ? sp.getUser().getId() : null);
                    performer.put("studentName", name.trim());
                    performer.put("score", Math.round(avgScore * 100.0) / 100.0);
                    performer.put("departmentName", sp.getDepartment() != null ? sp.getDepartment().getName() : "Unknown");
                    return performer;
                })
                .sorted(Comparator.comparingDouble(a -> (Double) a.get("score")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getFacultyPerformance() {
        List<FacultyProfile> allFaculty = facultyProfileRepository.findAll();

        return allFaculty.stream()
                .map(fp -> {
                    String name = "";
                    if (fp.getUser() != null) {
                        name = (fp.getUser().getFirstName() != null ? fp.getUser().getFirstName() : "") +
                                (fp.getUser().getLastName() != null ? " " + fp.getUser().getLastName() : "");
                    }

                    long examCount = examRepository.findAll().stream()
                            .filter(e -> e.getCreatedBy() != null && e.getCreatedBy().getId().equals(fp.getUser().getId()))
                            .count();

                    Map<String, Object> perf = new HashMap<>();
                    perf.put("facultyId", fp.getUser() != null ? fp.getUser().getId() : null);
                    perf.put("facultyName", name.trim());
                    perf.put("totalExams", examCount);
                    perf.put("departmentName", fp.getDepartment() != null ? fp.getDepartment().getName() : "Unknown");
                    return perf;
                })
                .collect(Collectors.toList());
    }

    @Override
    public AdminAnalyticsResponse getAdminDashboard() {
        Map<String, Object> overall = getOverallStatistics();

        return AdminAnalyticsResponse.builder()
                .totalStudents(studentProfileRepository.count())
                .totalFaculty(facultyProfileRepository.count())
                .totalSubjects(subjectRepository.count())
                .totalExams(examRepository.count())
                .overallPassRate((Double) overall.get("overallPassRate"))
                .averageScore((Double) overall.get("averageScore"))
                .studentGrowth(getStudentGrowth())
                .departmentPerformance(getDepartmentPerformance())
                .topPerformers(getTopPerformersGlobal(10))
                .lowPerformers(getLowPerformersGlobal(10))
                .build();
    }
}
