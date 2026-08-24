package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnitPerformanceItem {

    private String unitName;
    private String subjectName;
    private Double accuracy;
    private Integer totalQuestions;
}
