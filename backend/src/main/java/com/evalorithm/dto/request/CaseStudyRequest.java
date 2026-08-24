package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseStudyRequest {

    private String scenario;

    private String subQuestions;
}
