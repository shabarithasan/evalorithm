package com.evalorithm.controller;

import com.evalorithm.dto.request.CertificateGenerateRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.CertificateResponse;
import com.evalorithm.service.CertificateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/certificates")
@RequiredArgsConstructor
@Tag(name = "Certificates", description = "Certificate generation and verification endpoints")
public class CertificateController {

    private final CertificateService certificateService;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY')")
    @Operation(summary = "Generate a certificate")
    public ResponseEntity<ApiResponse<CertificateResponse>> generate(
            @Valid @RequestBody CertificateGenerateRequest request,
            @RequestParam Long issuedBy) {
        CertificateResponse response = certificateService.generateCertificate(request, issuedBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Certificate generated", response));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student certificates")
    public ResponseEntity<ApiResponse<List<CertificateResponse>>> getStudentCertificates(@PathVariable Long studentId) {
        List<CertificateResponse> response = certificateService.getStudentCertificates(studentId);
        return ResponseEntity.ok(ApiResponse.success("Student certificates retrieved", response));
    }

    @GetMapping("/verify/{certNumber}")
    @Operation(summary = "Verify a certificate")
    public ResponseEntity<ApiResponse<CertificateResponse>> verify(@PathVariable String certNumber) {
        CertificateResponse response = certificateService.verifyCertificate(certNumber);
        return ResponseEntity.ok(ApiResponse.success("Certificate verified", response));
    }

    @GetMapping("/{id}/download")
    @Operation(summary = "Download certificate")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        byte[] data = certificateService.downloadCertificate(id);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certificate.html")
                .body(data);
    }
}
