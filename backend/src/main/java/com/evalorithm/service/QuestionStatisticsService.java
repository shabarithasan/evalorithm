package com.evalorithm.service;

import com.evalorithm.dto.response.QuestionStatisticsResponse;

public interface QuestionStatisticsService {

    void recordView(Long questionId);

    void recordUsage(Long questionId, boolean correct);

    QuestionStatisticsResponse getStatistics(Long questionId);
}
