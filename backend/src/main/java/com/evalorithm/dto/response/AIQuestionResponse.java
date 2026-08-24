package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIQuestionResponse {

    private Long id;
    private String questionText;
    private String questionType;
    private String difficulty;
    private String bloomLevel;
    private List<String> options;
    private String correctAnswer;
    private String explanation;
    private String subjectName;
    private String unitName;
    private String topicName;
    private Boolean isApproved;
    private Double confidenceScore;
    private LocalDateTime createdAt;
}
