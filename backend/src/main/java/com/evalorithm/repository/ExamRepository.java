package com.evalorithm.repository;

import com.evalorithm.entity.Exam;
import com.evalorithm.enums.ExamStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findByStatus(ExamStatus status);

    Page<Exam> findByStatus(ExamStatus status, Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE e.createdBy.id = :userId")
    List<Exam> findByCreatedById(@Param("userId") Long userId);

    long countByStatus(ExamStatus status);

    @Query("SELECT e FROM Exam e WHERE (:departmentId IS NULL OR e.department.id = :departmentId) " +
           "AND (:semesterId IS NULL OR e.semester.id = :semesterId) " +
           "AND (:subjectId IS NULL OR e.subject.id = :subjectId) " +
           "AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Exam> findByFilters(@Param("departmentId") Long departmentId,
                             @Param("semesterId") Long semesterId,
                             @Param("subjectId") Long subjectId,
                             @Param("search") String search,
                             Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE (:departmentId IS NULL OR e.department.id = :departmentId) " +
           "AND (:semesterId IS NULL OR e.semester.id = :semesterId) " +
           "AND (:subjectId IS NULL OR e.subject.id = :subjectId) " +
           "AND (:status IS NULL OR e.status = :status) " +
           "AND (:examType IS NULL OR e.examType = :examType) " +
           "AND (:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Exam> findByFiltersWithStatus(@Param("departmentId") Long departmentId,
                                       @Param("semesterId") Long semesterId,
                                       @Param("subjectId") Long subjectId,
                                       @Param("status") ExamStatus status,
                                       @Param("examType") com.evalorithm.enums.ExamType examType,
                                       @Param("search") String search,
                                       Pageable pageable);
}
