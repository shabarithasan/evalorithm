package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateResponse {

    private Long id;
    private String certificateType;
    private String studentName;
    private String registerNumber;
    private String examTitle;
    private String subjectName;
    private LocalDateTime issuedDate;
    private String certificateNumber;
    private String qrCode;
    private String issuedByName;
    private String digitalSignature;
}
