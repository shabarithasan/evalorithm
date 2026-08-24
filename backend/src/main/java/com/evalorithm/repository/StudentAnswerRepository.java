package com.evalorithm.repository;

import com.evalorithm.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.attempt.id = :attemptId")
    List<StudentAnswer> findByAttemptId(@Param("attemptId") Long attemptId);

    @Query("SELECT sa FROM StudentAnswer sa WHERE sa.attempt.id = :attemptId AND sa.examQuestion.id = :examQuestionId")
    Optional<StudentAnswer> findByAttemptIdAndExamQuestionId(@Param("attemptId") Long attemptId, @Param("examQuestionId") Long examQuestionId);

    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.attempt.id = :attemptId AND sa.isCorrect = true")
    long countByAttemptIdAndIsCorrectTrue(@Param("attemptId") Long attemptId);

    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.attempt.id = :attemptId AND sa.isCorrect = false")
    long countByAttemptIdAndIsCorrectFalse(@Param("attemptId") Long attemptId);
}
