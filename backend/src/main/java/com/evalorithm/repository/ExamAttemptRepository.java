package com.evalorithm.repository;

import com.evalorithm.entity.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.exam.id = :examId AND ea.studentProfile.id = :studentProfileId")
    List<ExamAttempt> findByExamIdAndStudentProfileId(@Param("examId") Long examId, @Param("studentProfileId") Long studentProfileId);

    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.exam.id = :examId AND ea.isActive = true")
    List<ExamAttempt> findByExamIdAndIsActiveTrue(@Param("examId") Long examId);

    @Query("SELECT COUNT(ea) FROM ExamAttempt ea WHERE ea.exam.id = :examId")
    long countByExamId(@Param("examId") Long examId);

    Optional<ExamAttempt> findByIdAndIsActiveTrue(Long id);
}
