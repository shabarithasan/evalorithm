package com.evalorithm.repository;

import com.evalorithm.entity.QuestionVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionVersionRepository extends JpaRepository<QuestionVersion, Long> {

    @Query("SELECT qv FROM QuestionVersion qv WHERE qv.question.id = :questionId ORDER BY qv.versionNumber DESC")
    List<QuestionVersion> findByQuestionIdOrderByVersionNumberDesc(@Param("questionId") Long questionId);
}
