package com.evalorithm.service;

import com.evalorithm.dto.response.ExamReportResponse;
import com.evalorithm.dto.response.ExamResultResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ExamReportService {

    ExamReportResponse getExamSummary(Long examId);

    List<Map<String, Object>> getQuestionWiseReport(Long examId);

    List<Map<String, Object>> getStudentWiseReport(Long examId);
}
