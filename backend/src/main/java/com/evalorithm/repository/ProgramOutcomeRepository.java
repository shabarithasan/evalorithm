package com.evalorithm.repository;

import com.evalorithm.entity.ProgramOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramOutcomeRepository extends JpaRepository<ProgramOutcome, Long> {

    @Query("SELECT po FROM ProgramOutcome po WHERE po.department.id = :departmentId")
    List<ProgramOutcome> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT po FROM ProgramOutcome po WHERE po.code = :code AND po.department.id = :departmentId")
    Optional<ProgramOutcome> findByCodeAndDepartmentId(@Param("code") String code, @Param("departmentId") Long departmentId);
}
