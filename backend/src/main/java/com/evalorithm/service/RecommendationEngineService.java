package com.evalorithm.service;

import com.evalorithm.dto.response.RecommendationResponse;

import java.util.List;

public interface RecommendationEngineService {

    List<RecommendationResponse> generateRecommendations(Long studentId);

    List<RecommendationResponse> getRecommendations(Long studentId);

    void markAsRead(Long recommendationId);

    void acceptRecommendation(Long recommendationId);
}
