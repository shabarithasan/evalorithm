package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramOutcomeRequest {

    @NotBlank(message = "PO code is required")
    private String code;

    @NotBlank(message = "PO name is required")
    private String name;

    private String description;

    @NotBlank(message = "Department ID is required")
    private String departmentId;
}
