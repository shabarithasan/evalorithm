package com.evalorithm.dto.response;

import com.evalorithm.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestionResponse {

    private Long id;
    private Long questionId;
    private String questionTitle;
    private QuestionType questionType;
    private Integer marks;
    private Integer orderNumber;
    private Boolean isActive;
    private String questionDescription;
    private String difficulty;
    private List<MCQOptionResponse> options;
}
