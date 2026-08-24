package com.evalorithm.repository;

import com.evalorithm.entity.PSOMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PSOMappingRepository extends JpaRepository<PSOMapping, Long> {

    @Query("SELECT pm FROM PSOMapping pm WHERE pm.pso.id = :psoId")
    List<PSOMapping> findByPsoId(@Param("psoId") Long psoId);

    @Query("SELECT pm FROM PSOMapping pm WHERE pm.co.id = :coId")
    List<PSOMapping> findByCoId(@Param("coId") Long coId);
}
