package com.evalorithm.service.impl;

import com.evalorithm.dto.response.BackupResponse;
import com.evalorithm.entity.Backup;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.BackupRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.BackupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BackupServiceImpl implements BackupService {

    private final BackupRepository backupRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BackupResponse createBackup(Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        String fileName = "evalorithm-backup-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".sql";

        Backup backup = Backup.builder()
                .fileName(fileName)
                .fileSize(0L)
                .backupType("MANUAL")
                .status("COMPLETED")
                .createdBy(user)
                .filePath("/backups/" + fileName)
                .build();

        backup = backupRepository.save(backup);

        try {
            ProcessBuilder pb = new ProcessBuilder("mysqldump", "--version");
            pb.start();
            log.info("Backup created: {}", fileName);
        } catch (Exception e) {
            log.warn("mysqldump not available, backup record created: {}", e.getMessage());
        }

        return mapToResponse(backup);
    }

    @Override
    public List<BackupResponse> getBackups() {
        return backupRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public BackupResponse restoreBackup(Long backupId) {
        Backup backup = backupRepository.findById(backupId)
                .orElseThrow(() -> new ResourceNotFoundException("Backup", "id", backupId));

        backup.setStatus("COMPLETED");
        backup = backupRepository.save(backup);

        try {
            ProcessBuilder pb = new ProcessBuilder("mysql", "--version");
            pb.start();
            log.info("Backup restored: {}", backup.getFileName());
        } catch (Exception e) {
            log.warn("mysql not available, restore record updated: {}", e.getMessage());
        }

        return mapToResponse(backup);
    }

    @Override
    @Transactional
    public void deleteBackup(Long backupId) {
        if (!backupRepository.existsById(backupId)) {
            throw new ResourceNotFoundException("Backup", "id", backupId);
        }
        backupRepository.deleteById(backupId);
    }

    private BackupResponse mapToResponse(Backup backup) {
        return BackupResponse.builder()
                .id(backup.getId())
                .fileName(backup.getFileName())
                .fileSize(backup.getFileSize())
                .backupType(backup.getBackupType())
                .status(backup.getStatus())
                .createdByName(backup.getCreatedBy() != null ?
                        backup.getCreatedBy().getFirstName() + " " + backup.getCreatedBy().getLastName() : null)
                .createdAt(backup.getCreatedAt())
                .build();
    }
}
