package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswerRequest {

    @NotNull(message = "Exam question ID is required")
    private Long examQuestionId;

    private String selectedOptionLabel;

    private String selectedOptionIds;

    private String textAnswer;

    private Integer timeTakenSeconds;
}
