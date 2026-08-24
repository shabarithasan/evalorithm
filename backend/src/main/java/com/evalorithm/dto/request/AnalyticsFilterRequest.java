package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsFilterRequest {

    private Long studentId;

    private Long subjectId;

    private Long departmentId;

    private Long semesterId;

    private String startDate;

    private String endDate;
}
