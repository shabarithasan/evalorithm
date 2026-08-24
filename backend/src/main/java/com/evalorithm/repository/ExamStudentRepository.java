package com.evalorithm.repository;

import com.evalorithm.entity.ExamStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamStudentRepository extends JpaRepository<ExamStudent, Long> {

    @Query("SELECT es FROM ExamStudent es WHERE es.exam.id = :examId")
    List<ExamStudent> findByExamId(@Param("examId") Long examId);

    @Query("SELECT es FROM ExamStudent es WHERE es.exam.id = :examId AND es.studentProfile.id = :studentProfileId")
    Optional<ExamStudent> findByExamIdAndStudentProfileId(@Param("examId") Long examId, @Param("studentProfileId") Long studentProfileId);

    @Query("SELECT CASE WHEN COUNT(es) > 0 THEN true ELSE false END FROM ExamStudent es WHERE es.exam.id = :examId AND es.studentProfile.id = :studentProfileId")
    boolean existsByExamIdAndStudentProfileId(@Param("examId") Long examId, @Param("studentProfileId") Long studentProfileId);

    @Modifying
    @Query("DELETE FROM ExamStudent es WHERE es.exam.id = :examId AND es.studentProfile.id IN :studentProfileIds")
    void deleteByExamIdAndStudentProfileIdIn(@Param("examId") Long examId, @Param("studentProfileIds") List<Long> studentProfileIds);
}
