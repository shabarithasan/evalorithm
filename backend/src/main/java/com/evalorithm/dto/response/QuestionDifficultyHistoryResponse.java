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
public class QuestionDifficultyHistoryResponse {

    private Long id;
    private Long questionId;
    private String difficulty;
    private Boolean wasCorrect;
    private Integer timeTakenSeconds;
    private LocalDateTime answeredAt;
}
