package com.evalorithm.service;

import com.evalorithm.dto.request.SubjectRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SubjectResponse;

import java.util.List;

import org.springframework.data.domain.Pageable;

public interface SubjectService {

    PageResponse<SubjectResponse> getAll(Pageable pageable);

    SubjectResponse getById(Long id);

    List<SubjectResponse> getByDepartment(Long departmentId);

    List<SubjectResponse> getBySemester(Long semesterId);

    SubjectResponse create(SubjectRequest request);

    SubjectResponse update(Long id, SubjectRequest request);

    void delete(Long id);
}
