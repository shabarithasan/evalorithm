package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdaptiveQuestionResponse {

    private Long questionId;
    private String questionText;
    private String questionType;
    private String difficulty;
    private List<String> options;
    private Integer marks;
    private Integer timeLimit;
}
