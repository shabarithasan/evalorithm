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
public class AIInsightResponse {

    private Long id;
    private String insightType;
    private String title;
    private String description;
    private String subjectName;
    private Double value;
    private LocalDateTime generatedAt;
}
