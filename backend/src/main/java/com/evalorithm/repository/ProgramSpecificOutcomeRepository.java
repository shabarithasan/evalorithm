package com.evalorithm.repository;

import com.evalorithm.entity.ProgramSpecificOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramSpecificOutcomeRepository extends JpaRepository<ProgramSpecificOutcome, Long> {

    @Query("SELECT pso FROM ProgramSpecificOutcome pso WHERE pso.department.id = :departmentId")
    List<ProgramSpecificOutcome> findByDepartmentId(@Param("departmentId") Long departmentId);
}
