package com.evalorithm.service;

import com.evalorithm.dto.request.ProgramOutcomeRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.ProgramOutcomeResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProgramOutcomeService {

    PageResponse<ProgramOutcomeResponse> getAll(Pageable pageable);

    ProgramOutcomeResponse getById(Long id);

    ProgramOutcomeResponse create(ProgramOutcomeRequest request);

    ProgramOutcomeResponse update(Long id, ProgramOutcomeRequest request);

    void delete(Long id);

    List<ProgramOutcomeResponse> getByDepartmentId(Long departmentId);
}
