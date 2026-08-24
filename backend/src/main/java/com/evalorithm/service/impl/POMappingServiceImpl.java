package com.evalorithm.service.impl;

import com.evalorithm.dto.request.POMappingRequest;
import com.evalorithm.dto.response.COMappingResponse;
import com.evalorithm.entity.CourseOutcome;
import com.evalorithm.entity.POMapping;
import com.evalorithm.entity.ProgramOutcome;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.CourseOutcomeRepository;
import com.evalorithm.repository.POMappingRepository;
import com.evalorithm.repository.ProgramOutcomeRepository;
import com.evalorithm.service.POMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class POMappingServiceImpl implements POMappingService {

    private final POMappingRepository poMappingRepository;
    private final ProgramOutcomeRepository programOutcomeRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;

    @Override
    @Transactional
    public void mapCOToPO(POMappingRequest request) {
        ProgramOutcome po = programOutcomeRepository.findById(request.getPoId())
                .orElseThrow(() -> new ResourceNotFoundException("ProgramOutcome", "id", request.getPoId()));

        CourseOutcome co = courseOutcomeRepository.findById(request.getCoId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseOutcome", "id", request.getCoId()));

        POMapping mapping = POMapping.builder()
                .po(po)
                .co(co)
                .weightage(request.getWeightage() != null ? request.getWeightage() : 100.0)
                .build();

        poMappingRepository.save(mapping);
    }

    @Override
    @Transactional
    public void removeMapping(Long mappingId) {
        if (!poMappingRepository.existsById(mappingId)) {
            throw new ResourceNotFoundException("POMapping", "id", mappingId);
        }
        poMappingRepository.deleteById(mappingId);
    }

    @Override
    public List<COMappingResponse> getPOsByDepartment(Long departmentId) {
        List<ProgramOutcome> pos = programOutcomeRepository.findByDepartmentId(departmentId);
        List<COMappingResponse> results = new ArrayList<>();
        for (ProgramOutcome po : pos) {
            List<POMapping> mappings = poMappingRepository.findByPoId(po.getId());
            for (POMapping m : mappings) {
                results.add(COMappingResponse.builder()
                        .id(m.getId())
                        .coCode(m.getCo() != null ? m.getCo().getCode() : null)
                        .questionTitle(po.getCode() + " - " + po.getName())
                        .questionType("PO")
                        .weightage(m.getWeightage())
                        .build());
            }
        }
        return results;
    }
}
