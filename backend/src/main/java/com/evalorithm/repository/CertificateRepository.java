package com.evalorithm.repository;

import com.evalorithm.entity.Certificate;
import com.evalorithm.enums.CertificateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    @Query("SELECT c FROM Certificate c WHERE c.studentProfile.id = :studentProfileId")
    List<Certificate> findByStudentProfileId(@Param("studentProfileId") Long studentProfileId);

    Optional<Certificate> findByCertificateNumber(String certificateNumber);

    List<Certificate> findByCertificateType(CertificateType certificateType);
}
