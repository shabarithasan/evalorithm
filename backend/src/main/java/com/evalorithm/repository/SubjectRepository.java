package com.evalorithm.repository;

import com.evalorithm.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query("SELECT s FROM Subject s WHERE s.department.id = :departmentId")
    List<Subject> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT s FROM Subject s WHERE s.semester.id = :semesterId")
    List<Subject> findBySemesterId(@Param("semesterId") Long semesterId);

    Optional<Subject> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT s FROM Subject s WHERE s.department.id = :departmentId AND s.semester.id = :semesterId")
    Page<Subject> findByDepartmentIdAndSemesterId(@Param("departmentId") Long departmentId, @Param("semesterId") Long semesterId, Pageable pageable);
}
