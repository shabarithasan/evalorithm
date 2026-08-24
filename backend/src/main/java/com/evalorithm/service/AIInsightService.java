package com.evalorithm.service;

import com.evalorithm.dto.response.AIInsightResponse;

import java.util.List;

public interface AIInsightService {

    List<AIInsightResponse> generateInsights(Long userId);

    List<AIInsightResponse> getInsights(Long userId);

    void markAsRead(Long insightId);
}
