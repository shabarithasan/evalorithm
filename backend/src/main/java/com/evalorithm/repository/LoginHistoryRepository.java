package com.evalorithm.repository;

import com.evalorithm.entity.LoginHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    @Query("SELECT lh FROM LoginHistory lh WHERE lh.user.id = :userId ORDER BY lh.loginTime DESC")
    Page<LoginHistory> findByUserIdOrderByLoginTimeDesc(@Param("userId") Long userId, Pageable pageable);

    List<LoginHistory> findByIsSuccessful(boolean isSuccessful);

    long countByLoginTimeAfter(LocalDateTime dateTime);

    List<LoginHistory> findByLogoutTimeIsNullAndIsSuccessfulTrue();
}
