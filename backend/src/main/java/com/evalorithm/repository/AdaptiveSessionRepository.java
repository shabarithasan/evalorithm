package com.evalorithm.repository;

import com.evalorithm.entity.AdaptiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdaptiveSessionRepository extends JpaRepository<AdaptiveSession, Long> {

    @Query("SELECT a FROM AdaptiveSession a WHERE a.studentProfile.id = :studentProfileId AND a.isActive = true")
    Optional<AdaptiveSession> findByStudentProfileIdAndIsActiveTrue(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT a FROM AdaptiveSession a WHERE a.studentProfile.id = :studentProfileId")
    List<AdaptiveSession> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT a FROM AdaptiveSession a WHERE a.subject.id = :subjectId")
    List<AdaptiveSession> findBySubjectId(@Param("subjectId") Long subjectId);
}
