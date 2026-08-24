package com.evalorithm.repository;

import com.evalorithm.entity.QuestionApproval;
import com.evalorithm.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionApprovalRepository extends JpaRepository<QuestionApproval, Long> {

    @Query("SELECT qa FROM QuestionApproval qa WHERE qa.question.id = :questionId")
    List<QuestionApproval> findByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT qa FROM QuestionApproval qa WHERE qa.question.id = :questionId AND qa.status = :status")
    List<QuestionApproval> findByQuestionIdAndStatus(@Param("questionId") Long questionId, @Param("status") ApprovalStatus status);

    @Query("SELECT qa FROM QuestionApproval qa WHERE qa.approver.id = :approverId AND qa.status = :status")
    List<QuestionApproval> findByApproverUserIdAndStatus(@Param("approverId") Long approverId, @Param("status") ApprovalStatus status);
}
