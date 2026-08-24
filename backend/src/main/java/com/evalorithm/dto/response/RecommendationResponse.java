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
public class RecommendationResponse {

    private Long id;
    private String type;
    private String title;
    private String description;
    private String priority;
    private String subjectName;
    private String topicName;
    private String unitName;
    private Boolean isRead;
    private LocalDateTime generatedAt;
}
