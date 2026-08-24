package com.evalorithm.repository;

import com.evalorithm.entity.Topic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    @Query("SELECT t FROM Topic t WHERE t.unit.id = :unitId")
    List<Topic> findByUnitId(@Param("unitId") Long unitId);

    @Query("SELECT t FROM Topic t WHERE t.unit.id = :unitId")
    Page<Topic> findByUnitId(@Param("unitId") Long unitId, Pageable pageable);

    @Query("SELECT t FROM Topic t WHERE LOWER(t.keywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Topic> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
