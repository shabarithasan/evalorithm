package com.evalorithm.service;

import com.evalorithm.dto.request.AdaptiveAnswerRequest;
import com.evalorithm.dto.response.AdaptiveQuestionResponse;
import com.evalorithm.dto.response.AdaptiveSessionResponse;
import com.evalorithm.dto.response.QuestionDifficultyHistoryResponse;

import java.util.List;

public interface AdaptiveTestingService {

    AdaptiveSessionResponse startAdaptiveSession(Long studentId, Long subjectId);

    AdaptiveQuestionResponse getNextQuestion(Long sessionId);

    AdaptiveQuestionResponse submitAnswer(Long sessionId, AdaptiveAnswerRequest request);

    AdaptiveSessionResponse endSession(Long sessionId);

    List<QuestionDifficultyHistoryResponse> getSessionHistory(Long sessionId);
}
