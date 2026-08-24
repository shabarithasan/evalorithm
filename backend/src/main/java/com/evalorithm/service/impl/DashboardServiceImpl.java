package com.evalorithm.service.impl;

import com.evalorithm.dto.response.DashboardResponse;
import com.evalorithm.dto.response.FacultyDashboardResponse;
import com.evalorithm.dto.response.StudentDashboardResponse;
import com.evalorithm.entity.FacultyProfile;
import com.evalorithm.entity.StudentProfile;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final FacultyProfileRepository facultyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardResponse getAdminDashboard() {
        return DashboardResponse.builder()
                .totalDepartments(departmentRepository.count())
                .totalSubjects(subjectRepository.count())
                .totalFaculty(facultyProfileRepository.count())
                .totalStudents(studentProfileRepository.count())
                .totalQuestions(0)
                .build();
    }

    @Override
    public FacultyDashboardResponse getFacultyDashboard(Long userId) {
        FacultyProfile profile = facultyProfileRepository.findByUserId(userId).orElse(null);

        return FacultyDashboardResponse.builder()
                .assignedSubjectsCount(profile != null ? profile.getAssignedSubjects().size() : 0)
                .questionCount(0)
                .pendingQuestions(0)
                .build();
    }

    @Override
    public StudentDashboardResponse getStudentDashboard(Long userId) {
        StudentProfile profile = studentProfileRepository.findByUserId(userId).orElse(null);

        return StudentDashboardResponse.builder()
                .enrolledSubjectsCount(profile != null ? profile.getEnrolledSubjects().size() : 0)
                .upcomingExams(0)
                .build();
    }
}
