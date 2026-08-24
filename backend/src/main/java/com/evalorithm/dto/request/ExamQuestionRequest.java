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
public class ExamQuestionRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotNull(message = "Marks is required")
    private Integer marks;

    private Integer orderNumber;
}
