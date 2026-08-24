package com.evalorithm.service.impl;

import com.evalorithm.dto.request.StudentRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.StudentResponse;
import com.evalorithm.dto.response.SubjectResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.Role;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.StudentService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<StudentResponse> getAll(Pageable pageable) {
        Page<StudentProfile> page = studentProfileRepository.findAll(pageable);
        List<StudentResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public StudentResponse getById(Long id) {
        StudentProfile profile = studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        return mapToResponse(profile);
    }

    @Override
    public List<StudentResponse> getByDepartment(Long departmentId) {
        return studentProfileRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<StudentResponse> getBySemester(Long semesterId) {
        return studentProfileRepository.findBySemesterId(semesterId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public StudentResponse create(StudentRequest request) {
        if (studentProfileRepository.existsByRegisterNumber(request.getRegisterNumber())) {
            throw new BadRequestException("Register number already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode("student123"))
                .phone(request.getPhone())
                .role(Role.ROLE_STUDENT)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        List<Subject> subjects = new ArrayList<>();
        if (request.getEnrolledSubjectIds() != null) {
            subjects = subjectRepository.findAllById(request.getEnrolledSubjectIds());
        }

        StudentProfile profile = StudentProfile.builder()
                .user(user)
                .registerNumber(request.getRegisterNumber())
                .department(department)
                .semester(semester)
                .enrolledSubjects(subjects)
                .build();

        profile = studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public StudentResponse update(Long id, StudentRequest request) {
        StudentProfile profile = studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            profile.setDepartment(department);
        }
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            profile.setSemester(semester);
        }

        User user = profile.getUser();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);

        profile = studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StudentProfile profile = studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));
        studentProfileRepository.delete(profile);
    }

    @Override
    @Transactional
    public StudentResponse enrollSubjects(Long id, List<Long> subjectIds) {
        StudentProfile profile = studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        List<Subject> newSubjects = subjectRepository.findAllById(subjectIds);
        for (Subject subject : newSubjects) {
            if (!profile.getEnrolledSubjects().contains(subject)) {
                profile.getEnrolledSubjects().add(subject);
            }
        }

        profile = studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public StudentResponse unenrollSubjects(Long id, List<Long> subjectIds) {
        StudentProfile profile = studentProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        profile.getEnrolledSubjects().removeIf(subject -> subjectIds.contains(subject.getId()));

        profile = studentProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    private StudentResponse mapToResponse(StudentProfile profile) {
        List<SubjectResponse> subjectResponses = profile.getEnrolledSubjects().stream()
                .map(subject -> SubjectResponse.builder()
                        .id(subject.getId())
                        .code(subject.getCode())
                        .name(subject.getName())
                        .departmentId(subject.getDepartment().getId())
                        .departmentName(subject.getDepartment().getName())
                        .semesterId(subject.getSemester().getId())
                        .semesterNumber(subject.getSemester().getNumber())
                        .credits(subject.getCredits())
                        .description(subject.getDescription())
                        .status(subject.getStatus())
                        .createdAt(subject.getCreatedAt())
                        .build())
                .toList();

        return StudentResponse.builder()
                .id(profile.getId())
                .registerNumber(profile.getRegisterNumber())
                .userId(profile.getUser().getId())
                .email(profile.getUser().getEmail())
                .firstName(profile.getUser().getFirstName())
                .lastName(profile.getUser().getLastName())
                .phone(profile.getUser().getPhone())
                .departmentId(profile.getDepartment() != null ? profile.getDepartment().getId() : null)
                .departmentName(profile.getDepartment() != null ? profile.getDepartment().getName() : null)
                .semesterId(profile.getSemester() != null ? profile.getSemester().getId() : null)
                .semesterNumber(profile.getSemester() != null ? profile.getSemester().getNumber() : null)
                .enrolledSubjects(subjectResponses)
                .build();
    }
}
