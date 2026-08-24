package com.evalorithm.service;

import com.evalorithm.dto.request.AdaptiveExamRequest;
import com.evalorithm.dto.response.AdaptiveExamResponse;

public interface AdaptiveExamService {

    AdaptiveExamResponse createAdaptiveExam(AdaptiveExamRequest request);

    AdaptiveExamResponse.AdaptiveQuestion getNextAdaptiveQuestion(Long attemptId, boolean previousCorrect, Long previousQuestionId);
}