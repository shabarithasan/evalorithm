package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogFilterRequest {

    private Long userId;

    private String action;

    private String entityName;

    private String startDate;

    private String endDate;
}
