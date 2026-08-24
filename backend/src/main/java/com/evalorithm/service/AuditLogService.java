package com.evalorithm.service;

import com.evalorithm.dto.request.AuditLogFilterRequest;
import com.evalorithm.dto.response.AuditLogResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.enums.AuditAction;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuditLogService {

    void log(AuditAction action, String entityName, Long entityId, String description, String ip, String userAgent, Long userId);

    PageResponse<AuditLogResponse> getAuditLogs(AuditLogFilterRequest filter, Pageable pageable);

    List<AuditLogResponse> getRecentActivity(int limit);
}
