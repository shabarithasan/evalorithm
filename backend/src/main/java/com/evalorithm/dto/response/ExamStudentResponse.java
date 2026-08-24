package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamStudentResponse {

    private Long studentProfileId;
    private Long userId;
    private String studentName;
    private String registerNumber;
    private String email;
    private Boolean isEligible;
}
