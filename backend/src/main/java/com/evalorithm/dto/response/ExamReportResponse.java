package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamReportResponse {

    private String examTitle;
    private Integer totalStudents;
    private Integer appeared;
    private Integer passed;
    private Integer failed;
    private Double averageMarks;
    private Double highestMarks;
    private Double lowestMarks;
    private Double passPercentage;
}
