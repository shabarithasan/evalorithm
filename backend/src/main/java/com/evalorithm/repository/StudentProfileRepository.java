package com.evalorithm.repository;

import com.evalorithm.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.user.id = :userId")
    Optional<StudentProfile> findByUserId(@Param("userId") Long userId);

    Optional<StudentProfile> findByRegisterNumber(String registerNumber);

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.department.id = :departmentId")
    List<StudentProfile> findByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.semester.id = :semesterId")
    List<StudentProfile> findBySemesterId(@Param("semesterId") Long semesterId);

    @Query("SELECT sp FROM StudentProfile sp WHERE sp.department.id = :departmentId AND sp.semester.id = :semesterId")
    List<StudentProfile> findByDepartmentIdAndSemesterId(@Param("departmentId") Long departmentId, @Param("semesterId") Long semesterId);

    boolean existsByRegisterNumber(String registerNumber);
}
