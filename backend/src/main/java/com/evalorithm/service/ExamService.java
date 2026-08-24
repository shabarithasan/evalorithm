package com.evalorithm.service;

import com.evalorithm.dto.request.ExamRequest;
import com.evalorithm.dto.response.ExamDashboardResponse;
import com.evalorithm.dto.response.ExamDetailResponse;
import com.evalorithm.dto.response.ExamResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExamService {

    PageResponse<ExamResponse> getAllExams(Pageable pageable, String search, String status,
                                          String examType, Long departmentId);

    ExamDetailResponse getExamById(Long id);

    ExamResponse createExam(ExamRequest request, Long userId);

    ExamResponse updateExam(Long id, ExamRequest request);

    void deleteExam(Long id);

    ExamResponse cloneExam(Long id, Long userId);

    ExamResponse publishExam(Long id);

    ExamResponse archiveExam(Long id);

    ExamResponse cancelExam(Long id);

    void addQuestionsToExam(Long examId, List<com.evalorithm.dto.request.ExamQuestionRequest> questions);

    void removeQuestionFromExam(Long examId, Long questionId);

    void assignStudents(Long examId, List<Long> studentIds);

    void unassignStudents(Long examId, List<Long> studentIds);

    ExamDashboardResponse getExamDashboard();
}
