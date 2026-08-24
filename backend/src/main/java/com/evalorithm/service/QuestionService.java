package com.evalorithm.service;

import com.evalorithm.dto.request.QuestionApprovalRequest;
import com.evalorithm.dto.request.QuestionRequest;
import com.evalorithm.dto.request.QuestionSearchRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.QuestionDashboardResponse;
import com.evalorithm.dto.response.QuestionResponse;
import com.evalorithm.dto.response.QuestionVersionResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionService {

    PageResponse<QuestionResponse> getAllQuestions(QuestionSearchRequest searchRequest, Pageable pageable);

    QuestionResponse getQuestionById(Long id);

    QuestionResponse createQuestion(QuestionRequest request, Long userId);

    QuestionResponse updateQuestion(Long id, QuestionRequest request, Long userId);

    void deleteQuestion(Long id);

    QuestionResponse duplicateQuestion(Long id, Long userId);

    QuestionResponse archiveQuestion(Long id);

    QuestionResponse restoreQuestion(Long id);

    QuestionResponse submitForReview(Long id);

    QuestionResponse approveQuestion(Long id, Long approverId, QuestionApprovalRequest request);

    List<QuestionVersionResponse> getQuestionVersions(Long questionId);

    QuestionDashboardResponse getQuestionDashboard();
}
