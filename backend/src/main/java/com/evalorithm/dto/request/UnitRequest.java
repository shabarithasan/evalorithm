package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequest {

    @NotNull(message = "Unit number is required")
    private Integer number;

    @NotBlank(message = "Unit name is required")
    private String name;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private String description;
}
