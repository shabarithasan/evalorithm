package com.evalorithm.repository;

import com.evalorithm.entity.FacultyAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyAnalyticsRepository extends JpaRepository<FacultyAnalytics, Long> {

    @Query("SELECT fa FROM FacultyAnalytics fa WHERE fa.facultyProfile.id = :facultyProfileId")
    Optional<FacultyAnalytics> findByFacultyProfileId(@Param("facultyProfileId") Long facultyProfileId);

    @Query("SELECT fa FROM FacultyAnalytics fa WHERE fa.facultyProfile.id = :facultyProfileId AND fa.subject.id = :subjectId")
    Optional<FacultyAnalytics> findByFacultyProfileIdAndSubjectId(@Param("facultyProfileId") Long facultyProfileId, @Param("subjectId") Long subjectId);
}
