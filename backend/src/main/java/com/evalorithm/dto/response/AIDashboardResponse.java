package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIDashboardResponse {

    private Long aiGeneratedQuestions;
    private Long adaptiveExams;
    private Double studentPerformance;
    private Long weakTopicsCount;
    private Long strongTopicsCount;
    private Long recommendationsCount;
}
