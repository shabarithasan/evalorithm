package com.evalorithm.service.impl;

import com.evalorithm.dto.request.FacultyRequest;
import com.evalorithm.dto.response.FacultyResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SubjectResponse;
import com.evalorithm.entity.Department;
import com.evalorithm.entity.FacultyProfile;
import com.evalorithm.entity.Subject;
import com.evalorithm.entity.User;
import com.evalorithm.enums.Role;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.repository.FacultyProfileRepository;
import com.evalorithm.repository.SubjectRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.FacultyService;
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
public class FacultyServiceImpl implements FacultyService {

    private final FacultyProfileRepository facultyProfileRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public PageResponse<FacultyResponse> getAll(Pageable pageable) {
        Page<FacultyProfile> page = facultyProfileRepository.findAll(pageable);
        List<FacultyResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public FacultyResponse getById(Long id) {
        FacultyProfile profile = facultyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
        return mapToResponse(profile);
    }

    @Override
    public List<FacultyResponse> getByDepartment(Long departmentId) {
        return facultyProfileRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public FacultyResponse create(FacultyRequest request) {
        if (facultyProfileRepository.existsByFacultyId(request.getFacultyId())) {
            throw new BadRequestException("Faculty ID already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode("faculty123"))
                .phone(request.getPhone())
                .role(Role.ROLE_FACULTY)
                .enabled(true)
                .build();
        user = userRepository.save(user);

        List<Subject> subjects = new ArrayList<>();
        if (request.getAssignedSubjectIds() != null) {
            subjects = subjectRepository.findAllById(request.getAssignedSubjectIds());
        }

        FacultyProfile profile = FacultyProfile.builder()
                .user(user)
                .facultyId(request.getFacultyId())
                .department(department)
                .designation(request.getDesignation())
                .assignedSubjects(subjects)
                .build();

        profile = facultyProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public FacultyResponse update(Long id, FacultyRequest request) {
        FacultyProfile profile = facultyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            profile.setDepartment(department);
        }
        if (request.getDesignation() != null) profile.setDesignation(request.getDesignation());

        User user = profile.getUser();
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);

        profile = facultyProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FacultyProfile profile = facultyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
        facultyProfileRepository.delete(profile);
    }

    @Override
    @Transactional
    public FacultyResponse assignSubjects(Long id, List<Long> subjectIds) {
        FacultyProfile profile = facultyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        List<Subject> newSubjects = subjectRepository.findAllById(subjectIds);
        for (Subject subject : newSubjects) {
            if (!profile.getAssignedSubjects().contains(subject)) {
                profile.getAssignedSubjects().add(subject);
            }
        }

        profile = facultyProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    @Override
    @Transactional
    public FacultyResponse removeSubjects(Long id, List<Long> subjectIds) {
        FacultyProfile profile = facultyProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        profile.getAssignedSubjects().removeIf(subject -> subjectIds.contains(subject.getId()));

        profile = facultyProfileRepository.save(profile);
        return mapToResponse(profile);
    }

    private FacultyResponse mapToResponse(FacultyProfile profile) {
        List<SubjectResponse> subjectResponses = profile.getAssignedSubjects().stream()
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

        return FacultyResponse.builder()
                .id(profile.getId())
                .facultyId(profile.getFacultyId())
                .userId(profile.getUser().getId())
                .email(profile.getUser().getEmail())
                .firstName(profile.getUser().getFirstName())
                .lastName(profile.getUser().getLastName())
                .phone(profile.getUser().getPhone())
                .departmentId(profile.getDepartment() != null ? profile.getDepartment().getId() : null)
                .departmentName(profile.getDepartment() != null ? profile.getDepartment().getName() : null)
                .designation(profile.getDesignation())
                .assignedSubjects(subjectResponses)
                .build();
    }
}
