package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MCQOptionResponse {

    private Long id;
    private String optionLabel;
    private String optionText;
    private Boolean isCorrect;
    private String explanation;
}
