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
public class BackupResponse {

    private Long id;
    private String fileName;
    private Long fileSize;
    private String backupType;
    private String status;
    private String createdByName;
    private LocalDateTime createdAt;
}
