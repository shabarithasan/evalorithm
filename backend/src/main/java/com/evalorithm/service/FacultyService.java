package com.evalorithm.service;

import com.evalorithm.dto.request.FacultyRequest;
import com.evalorithm.dto.response.FacultyResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FacultyService {

    PageResponse<FacultyResponse> getAll(Pageable pageable);

    FacultyResponse getById(Long id);

    List<FacultyResponse> getByDepartment(Long departmentId);

    FacultyResponse create(FacultyRequest request);

    FacultyResponse update(Long id, FacultyRequest request);

    void delete(Long id);

    FacultyResponse assignSubjects(Long id, List<Long> subjectIds);

    FacultyResponse removeSubjects(Long id, List<Long> subjectIds);
}
