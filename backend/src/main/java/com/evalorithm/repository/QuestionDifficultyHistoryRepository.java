package com.evalorithm.repository;

import com.evalorithm.entity.QuestionDifficultyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDifficultyHistoryRepository extends JpaRepository<QuestionDifficultyHistory, Long> {

    @Query("SELECT qdh FROM QuestionDifficultyHistory qdh WHERE qdh.session.id = :sessionId")
    List<QuestionDifficultyHistory> findBySessionId(@Param("sessionId") Long sessionId);
}
