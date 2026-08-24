package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private String userName;
    private String action;
    private String entityName;
    private Long entityId;
    private String description;
    private String ipAddress;
    private LocalDateTime timestamp;
}
