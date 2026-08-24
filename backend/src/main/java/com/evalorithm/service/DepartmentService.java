package com.evalorithm.service;

import com.evalorithm.dto.request.DepartmentRequest;
import com.evalorithm.dto.response.DepartmentResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {

    PageResponse<DepartmentResponse> getAll(Pageable pageable, String search);

    DepartmentResponse getById(Long id);

    DepartmentResponse create(DepartmentRequest request);

    DepartmentResponse update(Long id, DepartmentRequest request);

    void delete(Long id);
}
