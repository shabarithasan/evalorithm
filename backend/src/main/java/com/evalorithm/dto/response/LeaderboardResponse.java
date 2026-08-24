package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardResponse {

    private Integer rank;
    private Long studentId;
    private String studentName;
    private String departmentName;
    private Double score;
    private Double accuracy;
    private Integer totalExams;
}
