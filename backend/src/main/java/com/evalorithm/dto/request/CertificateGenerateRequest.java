package com.evalorithm.dto.request;

import com.evalorithm.enums.CertificateType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateGenerateRequest {

    @NotNull(message = "Certificate type is required")
    private CertificateType certificateType;

    private Long studentId;

    private Long examId;

    private Long subjectId;
}
