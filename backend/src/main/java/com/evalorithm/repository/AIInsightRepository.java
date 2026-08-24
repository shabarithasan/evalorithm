package com.evalorithm.repository;

import com.evalorithm.entity.AIInsight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIInsightRepository extends JpaRepository<AIInsight, Long> {

    @Query("SELECT ai FROM AIInsight ai WHERE ai.user.id = :userId ORDER BY ai.generatedAt DESC")
    List<AIInsight> findByUserIdOrderByGeneratedAtDesc(@Param("userId") Long userId);

    @Query("SELECT ai FROM AIInsight ai WHERE ai.user.id = :userId AND ai.isRead = false")
    List<AIInsight> findByUserIdAndIsReadFalse(@Param("userId") Long userId);
}
