package com.evalorithm.repository;

import com.evalorithm.entity.POMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface POMappingRepository extends JpaRepository<POMapping, Long> {

    @Query("SELECT pm FROM POMapping pm WHERE pm.po.id = :poId")
    List<POMapping> findByPoId(@Param("poId") Long poId);

    @Query("SELECT pm FROM POMapping pm WHERE pm.co.id = :coId")
    List<POMapping> findByCoId(@Param("coId") Long coId);
}
