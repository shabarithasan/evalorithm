package com.evalorithm.dto.response;

import com.evalorithm.enums.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResponse {

    private Long id;
    private String title;
    private String description;
    private QuestionType questionType;
    private QuestionDifficulty difficulty;
    private BloomLevel bloomLevel;
    private Integer marks;
    private Integer estimatedTime;
    private String explanation;
    private String reference;
    private QuestionStatus status;

    private Long categoryId;
    private String categoryName;

    private Long departmentId;
    private String departmentName;

    private Long semesterId;
    private Integer semesterNumber;

    private Long subjectId;
    private String subjectName;

    private Long unitId;
    private Integer unitNumber;
    private String unitName;

    private Long topicId;
    private String topicName;

    private String courseOutcome;
    private String programOutcome;
    private String programSpecificOutcome;

    private Long createdById;
    private String createdByName;

    private Long updatedById;
    private String updatedByName;

    private Integer version;
    private Boolean isArchived;

    private List<MCQOptionResponse> mcqOptions;
    private ProgrammingQuestionResponse programmingQuestion;
    private CaseStudyResponse caseStudy;
    private QuestionStatisticsResponse statistics;
    private List<QuestionMediaResponse> media;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
