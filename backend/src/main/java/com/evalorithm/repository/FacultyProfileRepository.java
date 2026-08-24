package com.evalorithm.repository;

import com.evalorithm.entity.FacultyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyProfileRepository extends JpaRepository<FacultyProfile, Long> {

    @Query("SELECT fp FROM FacultyProfile fp WHERE fp.user.id = :userId")
    Optional<FacultyProfile> findByUserId(@Param("userId") Long userId);

    Optional<FacultyProfile> findByFacultyId(String facultyId);

    @Query("SELECT fp FROM FacultyProfile fp WHERE fp.department.id = :departmentId")
    List<FacultyProfile> findByDepartmentId(@Param("departmentId") Long departmentId);

    boolean existsByFacultyId(String facultyId);
}
