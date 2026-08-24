package com.evalorithm.dto.request;

import com.evalorithm.enums.BloomLevel;
import com.evalorithm.enums.QuestionDifficulty;
import com.evalorithm.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Question type is required")
    private QuestionType questionType;

    private QuestionDifficulty difficulty;

    private BloomLevel bloomLevel;

    private Integer marks;

    private Integer estimatedTime;

    private String explanation;

    private String reference;

    private Long categoryId;

    private Long departmentId;

    private Long semesterId;

    private Long subjectId;

    private Long unitId;

    private Long topicId;

    private String courseOutcome;

    private String programOutcome;

    private String programSpecificOutcome;

    private List<MCQOptionRequest> mcqOptions;

    private ProgrammingQuestionRequest programmingQuestion;

    private CaseStudyRequest caseStudy;
}
