package com.evalorithm.repository;

import com.evalorithm.entity.ExamResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {

    @Query("SELECT er FROM ExamResult er WHERE er.exam.id = :examId")
    List<ExamResult> findByExamId(@Param("examId") Long examId);

    @Query("SELECT er FROM ExamResult er WHERE er.exam.id = :examId")
    Page<ExamResult> findByExamId(@Param("examId") Long examId, Pageable pageable);

    @Query("SELECT er FROM ExamResult er WHERE er.exam.id = :examId AND er.studentProfile.id = :studentProfileId")
    Optional<ExamResult> findByExamIdAndStudentProfileId(@Param("examId") Long examId, @Param("studentProfileId") Long studentProfileId);

    @Query("SELECT er FROM ExamResult er WHERE er.studentProfile.id = :studentProfileId")
    List<ExamResult> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId);

    @Query("SELECT er FROM ExamResult er WHERE er.studentProfile.id = :studentProfileId")
    Page<ExamResult> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId, Pageable pageable);
}
