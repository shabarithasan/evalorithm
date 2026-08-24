package com.evalorithm.repository;

import com.evalorithm.entity.Unit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    @Query("SELECT u FROM Unit u WHERE u.subject.id = :subjectId")
    List<Unit> findBySubjectId(@Param("subjectId") Long subjectId);

    @Query("SELECT u FROM Unit u WHERE u.subject.id = :subjectId")
    Page<Unit> findBySubjectId(@Param("subjectId") Long subjectId, Pageable pageable);
}
