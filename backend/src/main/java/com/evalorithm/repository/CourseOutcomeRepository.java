package com.evalorithm.repository;

import com.evalorithm.entity.CourseOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseOutcomeRepository extends JpaRepository<CourseOutcome, Long> {

    @Query("SELECT co FROM CourseOutcome co WHERE co.subject.id = :subjectId")
    List<CourseOutcome> findBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT co FROM CourseOutcome co WHERE co.department.id = :departmentId")
    List<CourseOutcome> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT co FROM CourseOutcome co WHERE co.code = :code AND co.subject.id = :subjectId")
    Optional<CourseOutcome> findByCodeAndSubjectId(@Param("code") String code, @Param("subjectId") Long subjectId);
}
