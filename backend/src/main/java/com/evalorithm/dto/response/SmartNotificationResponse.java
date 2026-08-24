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
public class SmartNotificationResponse {

    private Long id;
    private String title;
    private String message;
    private String type;
    private String priority;
    private LocalDateTime generatedAt;
}
