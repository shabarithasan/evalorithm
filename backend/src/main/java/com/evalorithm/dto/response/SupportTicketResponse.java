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
public class SupportTicketResponse {

    private Long id;
    private String subject;
    private String description;
    private String status;
    private String priority;
    private String assignedToName;
    private String resolution;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
