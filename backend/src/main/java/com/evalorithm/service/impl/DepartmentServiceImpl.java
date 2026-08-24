package com.evalorithm.service.impl;

import com.evalorithm.dto.request.DepartmentRequest;
import com.evalorithm.dto.response.DepartmentResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.entity.Department;
import com.evalorithm.enums.Status;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.service.DepartmentService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    public PageResponse<DepartmentResponse> getAll(Pageable pageable, String search) {
        Page<Department> page;
        if (search != null && !search.isEmpty()) {
            page = departmentRepository.searchByNameOrCode(search, pageable);
        } else {
            page = departmentRepository.findAll(pageable);
        }
        List<DepartmentResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public DepartmentResponse getById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return mapToResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Department code already exists");
        }

        Department department = Department.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Status.ACTIVE)
                .build();

        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (request.getCode() != null) department.setCode(request.getCode());
        if (request.getName() != null) department.setName(request.getName());
        if (request.getDescription() != null) department.setDescription(request.getDescription());
        if (request.getStatus() != null) department.setStatus(request.getStatus());

        department = departmentRepository.save(department);
        return mapToResponse(department);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!departmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Department", "id", id);
        }
        departmentRepository.deleteById(id);
    }

    private DepartmentResponse mapToResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .code(department.getCode())
                .name(department.getName())
                .description(department.getDescription())
                .status(department.getStatus())
                .createdAt(department.getCreatedAt())
                .build();
    }
}
