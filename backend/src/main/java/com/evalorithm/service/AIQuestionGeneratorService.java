package com.evalorithm.service;

import com.evalorithm.dto.request.AIQuestionGenerateRequest;
import com.evalorithm.dto.response.AIDashboardResponse;
import com.evalorithm.dto.response.AIQuestionResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AIQuestionGeneratorService {

    List<AIQuestionResponse> generateQuestions(AIQuestionGenerateRequest request, Long userId);

    List<AIQuestionResponse> saveGeneratedQuestions(List<AIQuestionResponse> questions, Long userId);

    AIQuestionResponse approveQuestion(Long aiQuestionId, Long facultyId);

    AIQuestionResponse rejectQuestion(Long aiQuestionId, Long facultyId);

    PageResponse<AIQuestionResponse> getGeneratedQuestions(Pageable pageable, Long userId, Long subjectId);

    AIDashboardResponse getAIDashboard();
}
