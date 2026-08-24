package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PSOMappingRequest {

    @NotNull(message = "PSO ID is required")
    private Long psoId;

    @NotNull(message = "CO ID is required")
    private Long coId;

    private Double weightage;
}
