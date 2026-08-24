package com.evalorithm.service;

import com.evalorithm.dto.request.SemesterRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SemesterResponse;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface SemesterService {

    PageResponse<SemesterResponse> getAll(Pageable pageable);

    SemesterResponse getById(Long id);

    List<SemesterResponse> getByDepartment(Long departmentId);

    SemesterResponse create(SemesterRequest request);

    SemesterResponse update(Long id, SemesterRequest request);

    void delete(Long id);
}
