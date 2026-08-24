package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResponse {

    private Long id;
    private String subjectName;
    private Double predictedMarks;
    private String predictedGrade;
    private Double passProbability;
    private String riskLevel;
    private String suggestedImprovement;
    private Double confidenceLevel;
    private LocalDateTime generatedAt;
}
