package com.evalorithm.repository;

import com.evalorithm.entity.ProgrammingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProgrammingQuestionRepository extends JpaRepository<ProgrammingQuestion, Long> {

    @Query("SELECT pq FROM ProgrammingQuestion pq WHERE pq.question.id = :questionId")
    Optional<ProgrammingQuestion> findByQuestionId(@Param("questionId") Long questionId);
}
