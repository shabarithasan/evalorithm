package com.evalorithm.repository;

import com.evalorithm.entity.ExamNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamNotificationRepository extends JpaRepository<ExamNotification, Long> {

    @Query("SELECT en FROM ExamNotification en WHERE en.user.id = :userId ORDER BY en.sentAt DESC")
    List<ExamNotification> findByUserIdOrderBySentAtDesc(@Param("userId") Long userId);

    @Query("SELECT COUNT(en) FROM ExamNotification en WHERE en.user.id = :userId AND en.isRead = false")
    long countByUserIdAndIsReadFalse(@Param("userId") Long userId);
}
