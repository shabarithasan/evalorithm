package com.evalorithm.service.impl;

import com.evalorithm.dto.request.AuditLogFilterRequest;
import com.evalorithm.dto.response.AuditLogResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.entity.AuditLog;
import com.evalorithm.entity.User;
import com.evalorithm.enums.AuditAction;
import com.evalorithm.repository.AuditLogRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.AuditLogService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void log(AuditAction action, String entityName, Long entityId, String description, String ip, String userAgent, Long userId) {
        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .description(description)
                .ipAddress(ip)
                .userAgent(userAgent)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(auditLog);
    }

    @Override
    public PageResponse<AuditLogResponse> getAuditLogs(AuditLogFilterRequest filter, Pageable pageable) {
        Page<AuditLog> page;

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            LocalDateTime start = LocalDateTime.parse(filter.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(filter.getEndDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            page = auditLogRepository.findByTimestampBetween(start, end)
                    .stream().collect(java.util.stream.Collectors.collectingAndThen(
                            java.util.stream.Collectors.toList(),
                            list -> new org.springframework.data.domain.PageImpl<>(list, pageable, list.size())
                    ));
        } else if (filter.getAction() != null) {
            AuditAction action = AuditAction.valueOf(filter.getAction());
            List<AuditLog> logs = auditLogRepository.findByAction(action);
            page = new org.springframework.data.domain.PageImpl<>(logs, pageable, logs.size());
        } else if (filter.getEntityName() != null) {
            List<AuditLog> logs = auditLogRepository.findByEntityName(filter.getEntityName());
            page = new org.springframework.data.domain.PageImpl<>(logs, pageable, logs.size());
        } else {
            page = auditLogRepository.findAll(pageable);
        }

        List<AuditLogResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public List<AuditLogResponse> getRecentActivity(int limit) {
        List<AuditLog> logs = auditLogRepository.findTop20ByOrderByTimestampDesc();
        return logs.stream()
                .limit(limit)
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog log) {
        return AuditLogResponse.builder()
                .id(log.getId())
                .userName(log.getUser() != null ? log.getUser().getFirstName() + " " + log.getUser().getLastName() : "System")
                .action(log.getAction().name())
                .entityName(log.getEntityName())
                .entityId(log.getEntityId())
                .description(log.getDescription())
                .ipAddress(log.getIpAddress())
                .timestamp(log.getTimestamp())
                .build();
    }
}
