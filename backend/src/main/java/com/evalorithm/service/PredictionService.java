package com.evalorithm.service;

import com.evalorithm.dto.response.PredictionResponse;

import java.util.List;

public interface PredictionService {

    PredictionResponse predictPerformance(Long studentId, Long subjectId);

    List<PredictionResponse> getPredictions(Long studentId);

    List<PredictionResponse> getRiskStudents(Long subjectId);
}
