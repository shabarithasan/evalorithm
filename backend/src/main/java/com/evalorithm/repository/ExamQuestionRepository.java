package com.evalorithm.repository;

import com.evalorithm.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {

    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    List<ExamQuestion> findByExamId(@Param("examId") Long examId);

    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.exam.id = :examId AND eq.isActive = true")
    List<ExamQuestion> findByExamIdAndIsActiveTrue(@Param("examId") Long examId);

    @Query("SELECT COUNT(eq) FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    long countByExamId(@Param("examId") Long examId);

    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.exam.id = :examId ORDER BY eq.orderNumber ASC")
    List<ExamQuestion> findByExamIdOrderByOrderNumberAsc(@Param("examId") Long examId);

    @Modifying
    @Query("DELETE FROM ExamQuestion eq WHERE eq.exam.id = :examId AND eq.question.id = :questionId")
    void deleteByExamIdAndQuestionId(@Param("examId") Long examId, @Param("questionId") Long questionId);
}
