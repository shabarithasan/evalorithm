package com.evalorithm.dto.request;

import com.evalorithm.enums.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SemesterRequest {

    @NotNull(message = "Semester number is required")
    private Integer number;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private Status status;
}
