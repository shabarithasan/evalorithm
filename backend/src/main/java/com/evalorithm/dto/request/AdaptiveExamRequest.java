package com.evalorithm.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdaptiveExamRequest {

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @Min(value = 10, message = "At least 10 questions")
    @Min(value = 50, message = "At most 50 questions")
    private Integer totalQuestions = 25;

    @NotNull(message = "Created by user ID is required")
    private Long createdBy;
}