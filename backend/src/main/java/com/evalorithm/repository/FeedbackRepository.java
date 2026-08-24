package com.evalorithm.repository;

import com.evalorithm.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f WHERE f.fromUser.id = :fromUserId")
    List<Feedback> findByFromUserId(@Param("fromUserId") Long fromUserId);

    @Query("SELECT f FROM Feedback f WHERE f.toUser.id = :toUserId")
    List<Feedback> findByToUserId(@Param("toUserId") Long toUserId);

    @Query("SELECT f FROM Feedback f WHERE f.subject.id = :subjectId")
    List<Feedback> findBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT f FROM Feedback f WHERE f.exam.id = :examId")
    List<Feedback> findByExamId(@Param("examId") Long examId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.subject.id = :subjectId")
    Double avgRatingBySubjectId(@Param("subjectId") Long subjectId);
}
