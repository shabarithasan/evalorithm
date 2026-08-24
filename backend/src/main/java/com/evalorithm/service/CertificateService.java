package com.evalorithm.service;

import com.evalorithm.dto.request.CertificateGenerateRequest;
import com.evalorithm.dto.response.CertificateResponse;

import java.util.List;

public interface CertificateService {

    CertificateResponse generateCertificate(CertificateGenerateRequest request, Long issuedBy);

    List<CertificateResponse> getStudentCertificates(Long studentId);

    CertificateResponse verifyCertificate(String certificateNumber);

    byte[] downloadCertificate(Long certificateId);
}
