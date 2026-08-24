package com.evalorithm.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicRequest {

    @NotBlank(message = "Topic name is required")
    private String name;

    @NotNull(message = "Unit ID is required")
    private Long unitId;

    private String description;

    private String keywords;
}
