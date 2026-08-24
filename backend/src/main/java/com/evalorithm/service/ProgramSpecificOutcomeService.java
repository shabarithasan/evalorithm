package com.evalorithm.service;

import com.evalorithm.dto.request.ProgramSpecificOutcomeRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.ProgramSpecificOutcomeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramSpecificOutcomeService {

    PageResponse<ProgramSpecificOutcomeResponse> getAll(Pageable pageable);

    ProgramSpecificOutcomeResponse getById(Long id);

    ProgramSpecificOutcomeResponse create(ProgramSpecificOutcomeRequest request);

    ProgramSpecificOutcomeResponse update(Long id, ProgramSpecificOutcomeRequest request);

    void delete(Long id);

    List<ProgramSpecificOutcomeResponse> getByDepartmentId(Long departmentId);
}
