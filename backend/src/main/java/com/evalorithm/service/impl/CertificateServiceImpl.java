package com.evalorithm.service.impl;

import com.evalorithm.dto.request.CertificateGenerateRequest;
import com.evalorithm.dto.response.CertificateResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.CertificateType;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.CertificateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CertificateResponse generateCertificate(CertificateGenerateRequest request, Long issuedBy) {
        StudentProfile student = studentProfileRepository.findById(request.getStudentId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "id", request.getStudentId()));
        User issuer = userRepository.findById(issuedBy)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", issuedBy));

        Exam exam = null;
        if (request.getExamId() != null) {
            exam = examRepository.findById(request.getExamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", request.getExamId()));
        }

        Subject subject = null;
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        }

        String certNumber = generateCertificateNumber(request.getCertificateType());
        String qrData = String.format("{\"certificateNumber\":\"%s\",\"student\":\"%s\",\"date\":\"%s\",\"verifyUrl\":\"/verify/%s\"}",
                certNumber, student.getRegisterNumber(), LocalDateTime.now(), certNumber);
        String signature = UUID.randomUUID().toString();

        Certificate certificate = Certificate.builder()
                .certificateType(request.getCertificateType())
                .studentProfile(student)
                .exam(exam)
                .subject(subject)
                .issuedDate(LocalDateTime.now())
                .certificateNumber(certNumber)
                .qrCode(qrData)
                .issuedBy(issuer)
                .digitalSignature(signature)
                .build();

        certificate = certificateRepository.save(certificate);
        return mapToResponse(certificate);
    }

    @Override
    public List<CertificateResponse> getStudentCertificates(Long studentId) {
        return certificateRepository.findByStudentProfileId(studentId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public CertificateResponse verifyCertificate(String certificateNumber) {
        Certificate certificate = certificateRepository.findByCertificateNumber(certificateNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "certificateNumber", certificateNumber));
        return mapToResponse(certificate);
    }

    @Override
    public byte[] downloadCertificate(Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate", "id", certificateId));

        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body{font-family:Georgia;text-align:center;padding:40px;}");
        html.append("h1{font-size:28px;color:#1a237e;}");
        html.append("h2{font-size:18px;color:#333;}");
        html.append(".cert-number{font-size:12px;color:#666;margin-top:20px;}");
        html.append(".signature{margin-top:40px;}");
        html.append("</style></head><body>");
        html.append("<h1>Certificate of ").append(certificate.getCertificateType().name()).append("</h1>");
        html.append("<h2>This is to certify that</h2>");
        html.append("<p><strong>").append(certificate.getStudentProfile().getRegisterNumber()).append("</strong></p>");
        html.append("<h2>has been awarded this certificate</h2>");
        html.append("<p>Date: ").append(certificate.getIssuedDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))).append("</p>");
        html.append("<div class='cert-number'>Certificate No: ").append(certificate.getCertificateNumber()).append("</div>");
        html.append("<div class='signature'>Issued by: ").append(certificate.getIssuedBy().getFirstName()).append("</div>");
        html.append("</body></html>");

        return html.toString().getBytes();
    }

    private String generateCertificateNumber(CertificateType type) {
        String year = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        long count = certificateRepository.count() + 1;
        return String.format("EVL-%s-%04d", year, count);
    }

    private CertificateResponse mapToResponse(Certificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .certificateType(certificate.getCertificateType().name())
                .studentName(certificate.getStudentProfile().getUser().getFirstName() + " " + certificate.getStudentProfile().getUser().getLastName())
                .registerNumber(certificate.getStudentProfile().getRegisterNumber())
                .examTitle(certificate.getExam() != null ? certificate.getExam().getTitle() : null)
                .subjectName(certificate.getSubject() != null ? certificate.getSubject().getName() : null)
                .issuedDate(certificate.getIssuedDate())
                .certificateNumber(certificate.getCertificateNumber())
                .qrCode(certificate.getQrCode())
                .issuedByName(certificate.getIssuedBy().getFirstName() + " " + certificate.getIssuedBy().getLastName())
                .digitalSignature(certificate.getDigitalSignature())
                .build();
    }
}
