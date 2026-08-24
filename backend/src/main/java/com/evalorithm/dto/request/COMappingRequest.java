package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class COMappingRequest {

    @NotNull(message = "CO ID is required")
    private Long coId;

    @NotNull(message = "Question ID is required")
    private Long questionId;

    private Double weightage;
}
