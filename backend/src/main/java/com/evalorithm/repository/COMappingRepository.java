package com.evalorithm.repository;

import com.evalorithm.entity.COMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface COMappingRepository extends JpaRepository<COMapping, Long> {

    @Query("SELECT cm FROM COMapping cm WHERE cm.co.id = :coId")
    List<COMapping> findByCoId(@Param("coId") Long coId);

    @Query("SELECT cm FROM COMapping cm WHERE cm.question.id = :questionId")
    List<COMapping> findByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT cm FROM COMapping cm WHERE cm.co.subject.id = :subjectId")
    List<COMapping> findByCoSubjectId(@Param("subjectId") Long subjectId);
}
