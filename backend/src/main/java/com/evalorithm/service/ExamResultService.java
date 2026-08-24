package com.evalorithm.service;

import com.evalorithm.dto.response.ExamResultResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.StudentAnswerResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamResultService {

    ExamResultResponse getResult(Long examId, Long studentId);

    PageResponse<ExamResultResponse> getAllResultsForExam(Long examId, Pageable pageable);

    PageResponse<ExamResultResponse> getStudentResults(Long studentId, Pageable pageable);

    List<StudentAnswerResponse> getResultDetails(Long resultId);

    byte[] exportResults(Long examId);
}
