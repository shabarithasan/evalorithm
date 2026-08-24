package com.evalorithm.repository;

import com.evalorithm.entity.CaseStudy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaseStudyRepository extends JpaRepository<CaseStudy, Long> {

    @Query("SELECT cs FROM CaseStudy cs WHERE cs.question.id = :questionId")
    Optional<CaseStudy> findByQuestionId(@Param("questionId") Long questionId);
}
