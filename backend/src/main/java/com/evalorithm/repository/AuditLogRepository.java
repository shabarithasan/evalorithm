package com.evalorithm.repository;

import com.evalorithm.entity.AuditLog;
import com.evalorithm.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId ORDER BY al.timestamp DESC")
    List<AuditLog> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);

    List<AuditLog> findByAction(AuditAction action);

    List<AuditLog> findByEntityName(String entityName);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findTop20ByOrderByTimestampDesc();
}
