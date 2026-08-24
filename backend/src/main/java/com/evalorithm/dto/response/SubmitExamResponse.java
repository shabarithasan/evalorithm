package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitExamResponse {

    private Long attemptId;
    private Integer totalAnswered;
    private Integer totalCorrect;
    private Integer totalWrong;
    private Integer totalSkipped;
    private Boolean autoEvaluated;
    private String message;
}
