package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseOutcomeResponse {

    private Long id;
    private String code;
    private String description;
    private String subjectName;
    private String departmentName;
    private Integer semesterNumber;
    private String bloomsLevel;
    private Boolean isAttainable;
    private Integer mappingCount;
}
