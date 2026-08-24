package com.evalorithm.service;

import com.evalorithm.dto.response.BackupResponse;

import java.util.List;

public interface BackupService {

    BackupResponse createBackup(Long userId);

    List<BackupResponse> getBackups();

    BackupResponse restoreBackup(Long backupId);

    void deleteBackup(Long backupId);
}
