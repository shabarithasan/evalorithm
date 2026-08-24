package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class POMappingRequest {

    @NotNull(message = "PO ID is required")
    private Long poId;

    @NotNull(message = "CO ID is required")
    private Long coId;

    private Double weightage;
}
