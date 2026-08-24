package com.evalorithm.service.impl;

import com.evalorithm.dto.request.UnitRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.UnitResponse;
import com.evalorithm.entity.Subject;
import com.evalorithm.entity.Unit;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.SubjectRepository;
import com.evalorithm.repository.UnitRepository;
import com.evalorithm.service.UnitService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final SubjectRepository subjectRepository;

    @Override
    public PageResponse<UnitResponse> getAll(Pageable pageable) {
        Page<Unit> page = unitRepository.findAll(pageable);
        List<UnitResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public UnitResponse getById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", id));
        return mapToResponse(unit);
    }

    @Override
    public List<UnitResponse> getBySubject(Long subjectId) {
        return unitRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public UnitResponse create(UnitRequest request) {
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

        Unit unit = Unit.builder()
                .number(request.getNumber())
                .name(request.getName())
                .subject(subject)
                .description(request.getDescription())
                .build();

        unit = unitRepository.save(unit);
        return mapToResponse(unit);
    }

    @Override
    @Transactional
    public UnitResponse update(Long id, UnitRequest request) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", id));

        if (request.getNumber() != null) unit.setNumber(request.getNumber());
        if (request.getName() != null) unit.setName(request.getName());
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
            unit.setSubject(subject);
        }
        if (request.getDescription() != null) unit.setDescription(request.getDescription());

        unit = unitRepository.save(unit);
        return mapToResponse(unit);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!unitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Unit", "id", id);
        }
        unitRepository.deleteById(id);
    }

    private UnitResponse mapToResponse(Unit unit) {
        return UnitResponse.builder()
                .id(unit.getId())
                .number(unit.getNumber())
                .name(unit.getName())
                .subjectId(unit.getSubject().getId())
                .subjectName(unit.getSubject().getName())
                .description(unit.getDescription())
                .createdAt(unit.getCreatedAt())
                .build();
    }
}
