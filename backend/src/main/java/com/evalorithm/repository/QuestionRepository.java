package com.evalorithm.repository;

import com.evalorithm.entity.Question;
import com.evalorithm.enums.QuestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    Page<Question> findByStatus(QuestionStatus status, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.category.id = :categoryId")
    List<Question> findByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT q FROM Question q WHERE q.createdBy.id = :userId")
    List<Question> findByCreatedById(@Param("userId") Long userId);

    long countByStatus(QuestionStatus status);

    long countByIsArchived(Boolean isArchived);

    long countByCreatedAtAfter(LocalDateTime dateTime);

    @Query("SELECT q FROM Question q WHERE q.department.id = :departmentId AND q.semester.id = :semesterId AND q.subject.id = :subjectId")
    Page<Question> findByDepartmentIdAndSemesterIdAndSubjectId(@Param("departmentId") Long departmentId, @Param("semesterId") Long semesterId, @Param("subjectId") Long subjectId, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE " +
            "LOWER(q.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(q.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(q.reference) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Question> searchByTitleOrDescription(@Param("search") String search, Pageable pageable);

    List<Question> findBySubjectIdAndStatus(Long subjectId, QuestionStatus status);
}
