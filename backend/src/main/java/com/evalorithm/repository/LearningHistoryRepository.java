package com.evalorithm.repository;

import com.evalorithm.entity.LearningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LearningHistoryRepository extends JpaRepository<LearningHistory, Long> {

    @Query("SELECT lh FROM LearningHistory lh WHERE lh.studentProfile.id = :studentProfileId ORDER BY lh.recordedAt DESC")
    List<LearningHistory> findByStudentProfileIdOrderByRecordedAtDesc(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT lh FROM LearningHistory lh WHERE lh.studentProfile.id = :studentProfileId AND lh.subject.id = :subjectId")
    List<LearningHistory> findByStudentProfileIdAndSubjectId(@Param("studentProfileId") Long studentProfileId, @Param("subjectId") Long subjectId);

    @Query("SELECT lh FROM LearningHistory lh WHERE lh.studentProfile.id = :studentProfileId AND lh.recordedAt > :dateTime")
    List<LearningHistory> findByStudentProfileIdAndRecordedAtAfter(@Param("studentProfileId") Long studentProfileId, @Param("dateTime") LocalDateTime dateTime);
}
