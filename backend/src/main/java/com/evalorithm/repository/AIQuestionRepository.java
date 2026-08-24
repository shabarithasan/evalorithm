package com.evalorithm.repository;

import com.evalorithm.entity.AIQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AIQuestionRepository extends JpaRepository<AIQuestion, Long> {

    @Query("SELECT a FROM AIQuestion a WHERE a.subject.id = :subjectId AND a.isApproved = :isApproved")
    List<AIQuestion> findBySubjectIdAndIsApproved(@Param("subjectId") Long subjectId, @Param("isApproved") Boolean isApproved);

    @Query("SELECT a FROM AIQuestion a WHERE a.createdBy.id = :userId")
    List<AIQuestion> findByCreatedById(@Param("userId") Long userId);

    long countByIsApproved(Boolean isApproved);

    @Query("SELECT a FROM AIQuestion a WHERE a.department.id = :departmentId")
    List<AIQuestion> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT a FROM AIQuestion a WHERE a.createdBy.id = :userId")
    Page<AIQuestion> findByCreatedById(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT a FROM AIQuestion a WHERE a.createdBy.id = :userId AND a.subject.id = :subjectId")
    Page<AIQuestion> findByCreatedByIdAndSubjectId(@Param("userId") Long userId, @Param("subjectId") Long subjectId, Pageable pageable);

    @Query("SELECT a FROM AIQuestion a WHERE a.subject.id = :subjectId")
    Page<AIQuestion> findBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);
}
