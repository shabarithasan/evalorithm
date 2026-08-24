package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramOutcomeResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private String departmentName;
    private Integer mappingCount;
}
