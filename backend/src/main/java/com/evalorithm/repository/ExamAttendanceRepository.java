package com.evalorithm.repository;

import com.evalorithm.entity.ExamAttendance;
import com.evalorithm.enums.AttendanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamAttendanceRepository extends JpaRepository<ExamAttendance, Long> {

    @Query("SELECT ea FROM ExamAttendance ea WHERE ea.exam.id = :examId")
    List<ExamAttendance> findByExamId(@Param("examId") Long examId);

    @Query("SELECT ea FROM ExamAttendance ea WHERE ea.exam.id = :examId AND ea.status = :status")
    List<ExamAttendance> findByExamIdAndStatus(@Param("examId") Long examId, @Param("status") AttendanceStatus status);
}
