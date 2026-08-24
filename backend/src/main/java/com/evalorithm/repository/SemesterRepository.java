package com.evalorithm.repository;

import com.evalorithm.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    @Query("SELECT s FROM Semester s WHERE s.department.id = :departmentId")
    List<Semester> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT s FROM Semester s WHERE s.number = :number AND s.department.id = :departmentId")
    Optional<Semester> findByNumberAndDepartmentId(@Param("number") Integer number, @Param("departmentId") Long departmentId);

    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Semester s WHERE s.number = :number AND s.department.id = :departmentId")
    boolean existsByNumberAndDepartmentId(@Param("number") Integer number, @Param("departmentId") Long departmentId);
}
