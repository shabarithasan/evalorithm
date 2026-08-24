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
public class ExamResultResponse {

    private Long id;
    private String examTitle;
    private String studentName;
    private Double totalMarksObtained;
    private Integer totalMarksPossible;
    private Double percentage;
    private String grade;
    private Boolean isPassed;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Integer skippedQuestions;
    private Integer timeTakenMinutes;
    private LocalDateTime evaluatedAt;
}
