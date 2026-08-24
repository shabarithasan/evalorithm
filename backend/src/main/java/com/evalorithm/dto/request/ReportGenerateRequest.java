package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportGenerateRequest {

    private String reportType;

    private Long entityId;

    private String format;

    private String startDate;

    private String endDate;
}
