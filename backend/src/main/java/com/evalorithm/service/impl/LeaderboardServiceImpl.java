package com.evalorithm.service.impl;

import com.evalorithm.dto.response.LeaderboardResponse;
import com.evalorithm.entity.*;
import com.evalorithm.repository.*;
import com.evalorithm.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private final StudentProfileRepository studentProfileRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final ExamResultRepository examResultRepository;
    private final ExamRepository examRepository;

    @Override
    public List<Map<String, Object>> getDepartmentLeaderboard() {
        List<Department> departments = departmentRepository.findAll();

        return departments.stream()
                .map(dept -> {
                    List<StudentProfile> deptStudents = studentProfileRepository.findByDepartmentId(dept.getId());
                    double avgScore = deptStudents.stream()
                            .flatMap(sp -> examResultRepository.findByStudentProfileId(sp.getId()).stream())
                            .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                            .average()
                            .orElse(0.0);

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("departmentId", dept.getId());
                    entry.put("departmentName", dept.getName());
                    entry.put("totalStudents", deptStudents.size());
                    entry.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
                    return entry;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("averageScore"), (Double) a.get("averageScore")))
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaderboardResponse> getStudentLeaderboard(int limit) {
        List<StudentProfile> allStudents = studentProfileRepository.findAll();

        int[] rank = {1};
        return allStudents.stream()
                .map(sp -> {
                    double avgScore = examResultRepository.findByStudentProfileId(sp.getId()).stream()
                            .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                            .average()
                            .orElse(0.0);

                    long totalExams = examResultRepository.findByStudentProfileId(sp.getId()).size();

                    double accuracy = examResultRepository.findByStudentProfileId(sp.getId()).stream()
                            .filter(er -> Boolean.TRUE.equals(er.getIsPassed()))
                            .count();
                    double accuracyPct = totalExams > 0 ? accuracy / totalExams * 100 : 0;

                    String name = "";
                    if (sp.getUser() != null) {
                        name = (sp.getUser().getFirstName() != null ? sp.getUser().getFirstName() : "") +
                                (sp.getUser().getLastName() != null ? " " + sp.getUser().getLastName() : "");
                    }

                    return LeaderboardResponse.builder()
                            .rank(rank[0]++)
                            .studentId(sp.getUser() != null ? sp.getUser().getId() : null)
                            .studentName(name.trim())
                            .departmentName(sp.getDepartment() != null ? sp.getDepartment().getName() : "Unknown")
                            .score(Math.round(avgScore * 100.0) / 100.0)
                            .accuracy(Math.round(accuracyPct * 100.0) / 100.0)
                            .totalExams((int) totalExams)
                            .build();
                })
                .sorted(Comparator.comparingDouble(LeaderboardResponse::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getFacultyLeaderboard() {
        List<FacultyProfile> allFaculty = facultyProfileRepository.findAll();

        return allFaculty.stream()
                .map(fp -> {
                    long examCount = examRepository.findAll().stream()
                            .filter(e -> e.getCreatedBy() != null && e.getCreatedBy().getId().equals(fp.getUser().getId()))
                            .count();

                    String name = "";
                    if (fp.getUser() != null) {
                        name = (fp.getUser().getFirstName() != null ? fp.getUser().getFirstName() : "") +
                                (fp.getUser().getLastName() != null ? " " + fp.getUser().getLastName() : "");
                    }

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("facultyId", fp.getUser() != null ? fp.getUser().getId() : null);
                    entry.put("facultyName", name.trim());
                    entry.put("departmentName", fp.getDepartment() != null ? fp.getDepartment().getName() : "Unknown");
                    entry.put("totalExams", examCount);
                    return entry;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("totalExams"), (Long) a.get("totalExams")))
                .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getSubjectLeaderboard() {
        List<Subject> subjects = subjectRepository.findAll();

        return subjects.stream()
                .map(subject -> {
                    long examCount = examRepository.findAll().stream()
                            .filter(e -> e.getSubject() != null && e.getSubject().getId().equals(subject.getId()))
                            .count();

                    double avgScore = examRepository.findAll().stream()
                            .filter(e -> e.getSubject() != null && e.getSubject().getId().equals(subject.getId()))
                            .flatMap(e -> examResultRepository.findByExamId(e.getId()).stream())
                            .mapToDouble(er -> er.getPercentage() != null ? er.getPercentage() : 0.0)
                            .average()
                            .orElse(0.0);

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("subjectId", subject.getId());
                    entry.put("subjectName", subject.getName());
                    entry.put("totalExams", examCount);
                    entry.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
                    return entry;
                })
                .sorted((a, b) -> Double.compare((Double) b.get("averageScore"), (Double) a.get("averageScore")))
                .collect(Collectors.toList());
    }
}
