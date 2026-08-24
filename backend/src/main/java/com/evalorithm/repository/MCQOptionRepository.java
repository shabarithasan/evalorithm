package com.evalorithm.repository;

import com.evalorithm.entity.MCQOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MCQOptionRepository extends JpaRepository<MCQOption, Long> {

    @Query("SELECT mo FROM MCQOption mo WHERE mo.question.id = :questionId")
    List<MCQOption> findByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT mo FROM MCQOption mo WHERE mo.question.id = :questionId AND mo.isCorrect = true")
    List<MCQOption> findByQuestionIdAndIsCorrectTrue(@Param("questionId") Long questionId);
}
