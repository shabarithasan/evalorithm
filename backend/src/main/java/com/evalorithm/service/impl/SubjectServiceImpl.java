package com.evalorithm.service.impl;

import com.evalorithm.dto.request.SubjectRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SubjectResponse;
import com.evalorithm.entity.Department;
import com.evalorithm.entity.Semester;
import com.evalorithm.entity.Subject;
import com.evalorithm.enums.Status;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.repository.SemesterRepository;
import com.evalorithm.repository.SubjectRepository;
import com.evalorithm.service.SubjectService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;

    @Override
    public PageResponse<SubjectResponse> getAll(Pageable pageable) {
        Page<Subject> page = subjectRepository.findAll(pageable);
        List<SubjectResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public SubjectResponse getById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
        return mapToResponse(subject);
    }

    @Override
    public List<SubjectResponse> getByDepartment(Long departmentId) {
        return subjectRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SubjectResponse> getBySemester(Long semesterId) {
        return subjectRepository.findBySemesterId(semesterId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        if (subjectRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Subject code already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        Semester semester = semesterRepository.findById(request.getSemesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        Subject subject = Subject.builder()
                .code(request.getCode())
                .name(request.getName())
                .department(department)
                .semester(semester)
                .credits(request.getCredits())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Status.ACTIVE)
                .build();

        subject = subjectRepository.save(subject);
        return mapToResponse(subject);
    }

    @Override
    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));

        if (request.getCode() != null) subject.setCode(request.getCode());
        if (request.getName() != null) subject.setName(request.getName());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            subject.setDepartment(department);
        }
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            subject.setSemester(semester);
        }
        if (request.getCredits() != null) subject.setCredits(request.getCredits());
        if (request.getDescription() != null) subject.setDescription(request.getDescription());
        if (request.getStatus() != null) subject.setStatus(request.getStatus());

        subject = subjectRepository.save(subject);
        return mapToResponse(subject);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subject", "id", id);
        }
        subjectRepository.deleteById(id);
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
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
                .build();
    }
}
