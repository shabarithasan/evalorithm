package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOutcomeRequest {

    @NotBlank(message = "CO code is required")
    private String code;

    private String description;

    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    @NotBlank(message = "Department ID is required")
    private String departmentId;

    @NotBlank(message = "Semester ID is required")
    private String semesterId;

    private String bloomsLevel;

    private Boolean isAttainable;
}
