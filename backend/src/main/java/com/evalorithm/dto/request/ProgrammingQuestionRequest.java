package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammingQuestionRequest {

    private String problemStatement;

    private String inputFormat;

    private String outputFormat;

    private String constraints;

    private String sampleInput;

    private String sampleOutput;

    private String testCases;

    private String starterCode;

    private String solutionCode;

    private String programmingLanguage;
}
