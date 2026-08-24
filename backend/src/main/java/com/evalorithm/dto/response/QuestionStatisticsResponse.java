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
public class QuestionStatisticsResponse {

    private Long id;
    private Integer viewCount;
    private Integer usageCount;
    private Integer correctCount;
    private Integer wrongCount;
    private Double correctPercentage;
    private Double wrongPercentage;
    private LocalDateTime lastUsedAt;
}
