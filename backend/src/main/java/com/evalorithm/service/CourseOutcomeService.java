package com.evalorithm.service;

import com.evalorithm.dto.request.CourseOutcomeRequest;
import com.evalorithm.dto.response.CourseOutcomeResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseOutcomeService {

    PageResponse<CourseOutcomeResponse> getAll(Pageable pageable);

    CourseOutcomeResponse getById(Long id);

    CourseOutcomeResponse create(CourseOutcomeRequest request);

    CourseOutcomeResponse update(Long id, CourseOutcomeRequest request);

    void delete(Long id);

    List<CourseOutcomeResponse> getBySubjectId(Long subjectId);

    List<CourseOutcomeResponse> getByDepartmentId(Long departmentId);
}
