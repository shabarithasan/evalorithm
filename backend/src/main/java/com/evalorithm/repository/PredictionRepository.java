package com.evalorithm.repository;

import com.evalorithm.entity.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PredictionRepository extends JpaRepository<Prediction, Long> {

    @Query("SELECT p FROM Prediction p WHERE p.studentProfile.id = :studentProfileId AND p.subject.id = :subjectId")
    Optional<Prediction> findByStudentProfileIdAndSubjectId(@Param("studentProfileId") Long studentProfileId, @Param("subjectId") Long subjectId);

    @Query("SELECT p FROM Prediction p WHERE p.studentProfile.id = :studentProfileId")
    List<Prediction> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId);
}
