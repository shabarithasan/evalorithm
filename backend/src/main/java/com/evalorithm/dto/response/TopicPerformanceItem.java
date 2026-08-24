package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopicPerformanceItem {

    private String topicName;
    private String unitName;
    private Double accuracy;
    private Integer totalQuestions;
}
