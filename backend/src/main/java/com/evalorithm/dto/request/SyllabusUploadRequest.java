package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyllabusUploadRequest {

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;
}
