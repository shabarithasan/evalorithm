package com.evalorithm.service.impl;

import com.evalorithm.dto.request.SemesterRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SemesterResponse;
import com.evalorithm.entity.Department;
import com.evalorithm.entity.Semester;
import com.evalorithm.enums.Status;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.repository.SemesterRepository;
import com.evalorithm.service.SemesterService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {

    private final SemesterRepository semesterRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public PageResponse<SemesterResponse> getAll(Pageable pageable) {
        Page<Semester> page = semesterRepository.findAll(pageable);
        List<SemesterResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public SemesterResponse getById(Long id) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));
        return mapToResponse(semester);
    }

    @Override
    public List<SemesterResponse> getByDepartment(Long departmentId) {
        return semesterRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public SemesterResponse create(SemesterRequest request) {
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        if (semesterRepository.existsByNumberAndDepartmentId(request.getNumber(), request.getDepartmentId())) {
            throw new BadRequestException("Semester number already exists for this department");
        }

        Semester semester = Semester.builder()
                .number(request.getNumber())
                .department(department)
                .status(request.getStatus() != null ? request.getStatus() : Status.ACTIVE)
                .build();

        semester = semesterRepository.save(semester);
        return mapToResponse(semester);
    }

    @Override
    @Transactional
    public SemesterResponse update(Long id, SemesterRequest request) {
        Semester semester = semesterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", id));

        if (request.getNumber() != null) semester.setNumber(request.getNumber());
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            semester.setDepartment(department);
        }
        if (request.getStatus() != null) semester.setStatus(request.getStatus());

        semester = semesterRepository.save(semester);
        return mapToResponse(semester);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!semesterRepository.existsById(id)) {
            throw new ResourceNotFoundException("Semester", "id", id);
        }
        semesterRepository.deleteById(id);
    }

    private SemesterResponse mapToResponse(Semester semester) {
        return SemesterResponse.builder()
                .id(semester.getId())
                .number(semester.getNumber())
                .departmentId(semester.getDepartment().getId())
                .departmentName(semester.getDepartment().getName())
                .status(semester.getStatus())
                .createdAt(semester.getCreatedAt())
                .build();
    }
}
