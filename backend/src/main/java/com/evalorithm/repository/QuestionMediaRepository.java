package com.evalorithm.repository;

import com.evalorithm.entity.QuestionMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionMediaRepository extends JpaRepository<QuestionMedia, Long> {

    @Query("SELECT qm FROM QuestionMedia qm WHERE qm.question.id = :questionId")
    List<QuestionMedia> findByQuestionId(@Param("questionId") Long questionId);
}
