package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class COMappingResponse {

    private Long id;
    private String coCode;
    private String questionTitle;
    private String questionType;
    private Double weightage;
}
