package com.evalorithm.repository;

import com.evalorithm.entity.Attainment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttainmentRepository extends JpaRepository<Attainment, Long> {

    @Query("SELECT a FROM Attainment a WHERE a.subject.id = :subjectId AND a.semester.id = :semesterId AND a.academicYear = :academicYear")
    List<Attainment> findBySubjectIdAndSemesterIdAndAcademicYear(@Param("subjectId") Long subjectId, @Param("semesterId") Long semesterId, @Param("academicYear") String academicYear);

    @Query("SELECT a FROM Attainment a WHERE a.subject.department.id = :departmentId AND a.academicYear = :academicYear")
    List<Attainment> findByDepartmentIdAndAcademicYear(@Param("departmentId") Long departmentId, @Param("academicYear") String academicYear);

    @Query("SELECT a FROM Attainment a WHERE a.co.id = :coId")
    List<Attainment> findByCoId(@Param("coId") Long coId);
}
