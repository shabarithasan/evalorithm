package com.evalorithm.service.impl;

import com.evalorithm.dto.request.ProgramOutcomeRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.ProgramOutcomeResponse;
import com.evalorithm.entity.Department;
import com.evalorithm.entity.ProgramOutcome;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.DepartmentRepository;
import com.evalorithm.repository.ProgramOutcomeRepository;
import com.evalorithm.service.ProgramOutcomeService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramOutcomeServiceImpl implements ProgramOutcomeService {

    private final ProgramOutcomeRepository programOutcomeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public PageResponse<ProgramOutcomeResponse> getAll(Pageable pageable) {
        Page<ProgramOutcome> page = programOutcomeRepository.findAll(pageable);
        List<ProgramOutcomeResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public ProgramOutcomeResponse getById(Long id) {
        ProgramOutcome po = programOutcomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramOutcome", "id", id));
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public ProgramOutcomeResponse create(ProgramOutcomeRequest request) {
        Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId()))
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        ProgramOutcome po = ProgramOutcome.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .department(department)
                .build();

        po = programOutcomeRepository.save(po);
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public ProgramOutcomeResponse update(Long id, ProgramOutcomeRequest request) {
        ProgramOutcome po = programOutcomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProgramOutcome", "id", id));

        if (request.getCode() != null) po.setCode(request.getCode());
        if (request.getName() != null) po.setName(request.getName());
        if (request.getDescription() != null) po.setDescription(request.getDescription());

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(Long.parseLong(request.getDepartmentId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            po.setDepartment(department);
        }

        po = programOutcomeRepository.save(po);
        return mapToResponse(po);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!programOutcomeRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProgramOutcome", "id", id);
        }
        programOutcomeRepository.deleteById(id);
    }

    @Override
    public List<ProgramOutcomeResponse> getByDepartmentId(Long departmentId) {
        return programOutcomeRepository.findByDepartmentId(departmentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ProgramOutcomeResponse mapToResponse(ProgramOutcome po) {
        return ProgramOutcomeResponse.builder()
                .id(po.getId())
                .code(po.getCode())
                .name(po.getName())
                .description(po.getDescription())
                .departmentName(po.getDepartment() != null ? po.getDepartment().getName() : null)
                .mappingCount(po.getPoMappings() != null ? po.getPoMappings().size() : 0)
                .build();
    }
}
