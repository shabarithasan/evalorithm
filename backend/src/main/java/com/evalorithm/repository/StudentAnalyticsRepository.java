package com.evalorithm.repository;

import com.evalorithm.entity.StudentAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnalyticsRepository extends JpaRepository<StudentAnalytics, Long> {

    @Query("SELECT sa FROM StudentAnalytics sa WHERE sa.studentProfile.id = :studentProfileId")
    List<StudentAnalytics> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT sa FROM StudentAnalytics sa WHERE sa.studentProfile.id = :studentProfileId AND sa.subject.id = :subjectId")
    Optional<StudentAnalytics> findByStudentProfileIdAndSubjectId(@Param("studentProfileId") Long studentProfileId, @Param("subjectId") Long subjectId);

    @Query("SELECT sa FROM StudentAnalytics sa WHERE sa.subject.id = :subjectId")
    List<StudentAnalytics> findBySubjectId(@Param("subjectId") Long subjectId);
}
