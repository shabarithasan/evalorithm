package com.evalorithm.service;

import com.evalorithm.dto.request.StudentRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.StudentResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudentService {

    PageResponse<StudentResponse> getAll(Pageable pageable);

    StudentResponse getById(Long id);

    List<StudentResponse> getByDepartment(Long departmentId);

    List<StudentResponse> getBySemester(Long semesterId);

    StudentResponse create(StudentRequest request);

    StudentResponse update(Long id, StudentRequest request);

    void delete(Long id);

    StudentResponse enrollSubjects(Long id, List<Long> subjectIds);

    StudentResponse unenrollSubjects(Long id, List<Long> subjectIds);
}
