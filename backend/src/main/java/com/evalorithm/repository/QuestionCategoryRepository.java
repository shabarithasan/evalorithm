package com.evalorithm.repository;

import com.evalorithm.entity.QuestionCategory;
import com.evalorithm.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionCategoryRepository extends JpaRepository<QuestionCategory, Long> {

    Optional<QuestionCategory> findByCategoryName(String categoryName);

    @Query("SELECT qc FROM QuestionCategory qc WHERE LOWER(qc.categoryName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<QuestionCategory> searchByName(@Param("search") String search, Pageable pageable);

    long countByStatus(Status status);

    boolean existsByCategoryName(String categoryName);
}
