package com.evalorithm.service.impl;

import com.evalorithm.dto.request.PSOMappingRequest;
import com.evalorithm.dto.response.COMappingResponse;
import com.evalorithm.entity.CourseOutcome;
import com.evalorithm.entity.PSOMapping;
import com.evalorithm.entity.ProgramSpecificOutcome;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.CourseOutcomeRepository;
import com.evalorithm.repository.PSOMappingRepository;
import com.evalorithm.repository.ProgramSpecificOutcomeRepository;
import com.evalorithm.service.PSOMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PSOMappingServiceImpl implements PSOMappingService {

    private final PSOMappingRepository psoMappingRepository;
    private final ProgramSpecificOutcomeRepository psoRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;

    @Override
    @Transactional
    public void mapCOToPSO(PSOMappingRequest request) {
        ProgramSpecificOutcome pso = psoRepository.findById(request.getPsoId())
                .orElseThrow(() -> new ResourceNotFoundException("ProgramSpecificOutcome", "id", request.getPsoId()));

        CourseOutcome co = courseOutcomeRepository.findById(request.getCoId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseOutcome", "id", request.getCoId()));

        PSOMapping mapping = PSOMapping.builder()
                .pso(pso)
                .co(co)
                .weightage(request.getWeightage() != null ? request.getWeightage() : 100.0)
                .build();

        psoMappingRepository.save(mapping);
    }

    @Override
    @Transactional
    public void removeMapping(Long mappingId) {
        if (!psoMappingRepository.existsById(mappingId)) {
            throw new ResourceNotFoundException("PSOMapping", "id", mappingId);
        }
        psoMappingRepository.deleteById(mappingId);
    }

    @Override
    public List<COMappingResponse> getPSOsByDepartment(Long departmentId) {
        List<ProgramSpecificOutcome> psos = psoRepository.findByDepartmentId(departmentId);
        List<COMappingResponse> results = new ArrayList<>();
        for (ProgramSpecificOutcome pso : psos) {
            List<PSOMapping> mappings = psoMappingRepository.findByPsoId(pso.getId());
            for (PSOMapping m : mappings) {
                results.add(COMappingResponse.builder()
                        .id(m.getId())
                        .coCode(m.getCo() != null ? m.getCo().getCode() : null)
                        .questionTitle(pso.getCode() + " - " + pso.getName())
                        .questionType("PSO")
                        .weightage(m.getWeightage())
                        .build());
            }
        }
        return results;
    }
}
