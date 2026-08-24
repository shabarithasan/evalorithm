package com.evalorithm.service;

import com.evalorithm.dto.request.UnitRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.UnitResponse;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UnitService {

    PageResponse<UnitResponse> getAll(Pageable pageable);

    UnitResponse getById(Long id);

    List<UnitResponse> getBySubject(Long subjectId);

    UnitResponse create(UnitRequest request);

    UnitResponse update(Long id, UnitRequest request);

    void delete(Long id);
}
