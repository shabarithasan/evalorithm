package com.evalorithm.service.impl;

import com.evalorithm.dto.request.ProgramSpecificOutcomeRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.ProgramSpecificOutcomeResponse;
import com.evalorithm.entity.Department;
import com.evalorithm.entity.ProgramSpecificOutcome;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.repository.ProgramSpecificOutcomeRepository;
import com.evalorithm.service.ProgramSpecificOutcomeService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramSpecificOutcomeServiceImpl implements ProgramSpecificOutcomeService {

    private final ProgramSpecificOutcomeRepository psoRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public PageResponse<ProgramSpecificOutcomeResponse> getAll(Pageable pageable) {
        Page<ProgramSpecificOutcome> page = psoRepository.findAll(pageable);
        List<ProgramSpecificOutcomeResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public ProgramSpecificOutcomeResponse getById(Long id) {
        ProgramSpecificOutcome pso = psoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSpecificOutcome", "id", id));
        return mapToResponse(pso);
    }

    @Override
    @Transactional
    public ProgramSpecificOutcomeResponse create(ProgramSpecificOutcomeRequest request) {
        Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId()))
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        ProgramSpecificOutcome pso = ProgramSpecificOutcome.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .department(department)
                .build();

        pso = psoRepository.save(pso);
        return mapToResponse(pso);
    }

    @Override
    @Transactional
    public ProgramSpecificOutcomeResponse update(Long id, ProgramSpecificOutcomeRequest request) {
        ProgramSpecificOutcome pso = psoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSpecificOutcome", "id", id));

        if (request.getCode() != null) pso.setCode(request.getCode());
        if (request.getName() != null) pso.setName(request.getName());
        if (request.getDescription() != null) pso.setDescription(request.getDescription());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            pso.setDepartment(department);
        }

        pso = psoRepository.save(pso);
        return mapToResponse(pso);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!psoRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProgramSpecificOutcome", "id", id);
        }
        psoRepository.deleteById(id);
    }

    @Override
    public List<ProgramSpecificOutcomeResponse> getByDepartmentId(Long departmentId) {
        return psoRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProgramSpecificOutcomeResponse mapToResponse(ProgramSpecificOutcome pso) {
        return ProgramSpecificOutcomeResponse.builder()
                .id(pso.getId())
                .code(pso.getCode())
                .name(pso.getName())
                .description(pso.getDescription())
                .departmentName(pso.getDepartment() != null ? pso.getDepartment().getName() : null)
                .mappingCount(pso.getPsoMappings() != null ? pso.getPsoMappings().size() : 0)
                .build();
    }
}
