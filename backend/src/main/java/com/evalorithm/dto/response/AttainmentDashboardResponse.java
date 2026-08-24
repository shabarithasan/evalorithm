package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttainmentDashboardResponse {

    private List<AttainmentResponse> attainments;
    private Double overallTarget;
    private Double overallActual;
    private Double percentageAchieved;
    private String departmentName;
}
