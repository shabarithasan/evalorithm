package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIQuestionGenerateRequest {

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private Long unitId;

    private Long topicId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Question type is required")
    private String questionType;

    @NotNull(message = "Difficulty is required")
    private String difficulty;

    private String bloomLevel;

    private Integer count = 5;

    private String additionalInstructions;
}
