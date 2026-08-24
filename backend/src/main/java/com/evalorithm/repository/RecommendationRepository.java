package com.evalorithm.repository;

import com.evalorithm.entity.Recommendation;
import com.evalorithm.enums.LearningPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    @Query("SELECT r FROM Recommendation r WHERE r.studentProfile.id = :studentProfileId ORDER BY r.generatedAt DESC")
    List<Recommendation> findByStudentProfileIdOrderByGeneratedAtDesc(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT r FROM Recommendation r WHERE r.studentProfile.id = :studentProfileId AND r.isRead = false")
    List<Recommendation> findByStudentProfileIdAndIsReadFalse(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT r FROM Recommendation r WHERE r.studentProfile.id = :studentProfileId AND r.priority = :priority")
    List<Recommendation> findByStudentProfileIdAndPriority(@Param("studentProfileId") Long studentProfileId, @Param("priority") LearningPriority priority);
}
