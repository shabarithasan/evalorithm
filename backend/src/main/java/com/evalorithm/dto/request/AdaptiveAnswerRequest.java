package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdaptiveAnswerRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotNull(message = "Session ID is required")
    private Long sessionId;

    private String selectedOption;

    private String textAnswer;

    private Integer timeTakenSeconds;
}
