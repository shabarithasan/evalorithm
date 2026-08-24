package com.evalorithm.repository;

import com.evalorithm.entity.Backup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

    List<Backup> findByBackupType(String backupType);

    List<Backup> findAllByOrderByCreatedAtDesc();
}
