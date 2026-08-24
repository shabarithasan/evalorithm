package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyAnalyticsResponse {

    private Long facultyId;
    private String facultyName;
    private String subjectName;
    private Integer totalExams;
    private Double averageClassScore;
    private Integer totalStudents;
    private Double passRate;
}
