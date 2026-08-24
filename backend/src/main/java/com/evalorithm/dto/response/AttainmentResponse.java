package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttainmentResponse {

    private Long id;
    private String coCode;
    private String coDescription;
    private String subjectName;
    private Integer semesterNumber;
    private String academicYear;
    private Double targetAttainment;
    private Double actualAttainment;
    private Double directAttainment;
    private Double indirectAttainment;
    private Boolean isAchieved;
}
