package com.evalorithm.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MCQOptionRequest {

    private String optionLabel;

    private String optionText;

    private Boolean isCorrect;

    private String explanation;
}
