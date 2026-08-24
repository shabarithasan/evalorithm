package com.evalorithm.service.impl;

import com.evalorithm.dto.request.CourseOutcomeRequest;
import com.evalorithm.dto.response.CourseOutcomeResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.entity.CourseOutcome;
import com.evalorithm.entity.Department;
import com.evalorithm.entity.Semester;
import com.evalorithm.entity.Subject;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.CourseOutcomeService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseOutcomeServiceImpl implements CourseOutcomeService {

    private final CourseOutcomeRepository courseOutcomeRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final SemesterRepository semesterRepository;
    private final COMappingRepository coMappingRepository;

    @Override
    public PageResponse<CourseOutcomeResponse> getAll(Pageable pageable) {
        Page<CourseOutcome> page = courseOutcomeRepository.findAll(pageable);
        List<CourseOutcomeResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public CourseOutcomeResponse getById(Long id) {
        CourseOutcome co = courseOutcomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseOutcome", "id", id));
        return mapToResponse(co);
    }

    @Override
    @Transactional
    public CourseOutcomeResponse create(CourseOutcomeRequest request) {
        Subject subject = subjectRepository.findById(Long.parseLong(request.getSubjectId()))
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId()))
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
        Semester semester = semesterRepository.findById(Long.parseLong(request.getSemesterId()))
                .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));

        CourseOutcome co = CourseOutcome.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .subject(subject)
                .department(department)
                .semester(semester)
                .bloomsLevel(request.getBloomsLevel())
                .isAttainable(request.getIsAttainable() != null ? request.getIsAttainable() : true)
                .build();

        co = courseOutcomeRepository.save(co);
        return mapToResponse(co);
    }

    @Override
    @Transactional
    public CourseOutcomeResponse update(Long id, CourseOutcomeRequest request) {
        CourseOutcome co = courseOutcomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CourseOutcome", "id", id));

        if (request.getCode() != null) co.setCode(request.getCode());
        if (request.getDescription() != null) co.setDescription(request.getDescription());
        if (request.getBloomsLevel() != null) co.setBloomsLevel(request.getBloomsLevel());
        if (request.getIsAttainable() != null) co.setIsAttainable(request.getIsAttainable());

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(Long.parseLong(request.getSubjectId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            co.setSubject(subject);
        }
        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            co.setDepartment(department);
        }
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(Long.parseLong(request.getSemesterId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Semester", "id", request.getSemesterId()));
            co.setSemester(semester);
        }

        co = courseOutcomeRepository.save(co);
        return mapToResponse(co);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!courseOutcomeRepository.existsById(id)) {
            throw new ResourceNotFoundException("CourseOutcome", "id", id);
        }
        courseOutcomeRepository.deleteById(id);
    }

    @Override
    public List<CourseOutcomeResponse> getBySubjectId(Long subjectId) {
        return courseOutcomeRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<CourseOutcomeResponse> getByDepartmentId(Long departmentId) {
        return courseOutcomeRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CourseOutcomeResponse mapToResponse(CourseOutcome co) {
        return CourseOutcomeResponse.builder()
                .id(co.getId())
                .code(co.getCode())
                .description(co.getDescription())
                .subjectName(co.getSubject() != null ? co.getSubject().getName() : null)
                .departmentName(co.getDepartment() != null ? co.getDepartment().getName() : null)
                .semesterNumber(co.getSemester() != null ? co.getSemester().getNumber() : null)
                .bloomsLevel(co.getBloomsLevel())
                .isAttainable(co.getIsAttainable())
                .mappingCount(co.getCoMappings() != null ? co.getCoMappings().size() : 0)
                .build();
    }
}
