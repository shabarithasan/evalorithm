package com.evalorithm.repository;

import com.evalorithm.entity.QuestionStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionStatisticsRepository extends JpaRepository<QuestionStatistics, Long> {

    @Query("SELECT qs FROM QuestionStatistics qs WHERE qs.question.id = :questionId")
    Optional<QuestionStatistics> findByQuestionId(@Param("questionId") Long questionId);
}
