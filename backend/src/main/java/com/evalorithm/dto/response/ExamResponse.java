package com.evalorithm.dto.response;

import com.evalorithm.enums.ExamStatus;
import com.evalorithm.enums.ExamType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExamResponse {

    private Long id;
    private String title;
    private String description;
    private ExamType examType;
    private ExamStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer durationMinutes;
    private Integer totalMarks;
    private Integer passingMarks;
    private Integer maxAttempts;
    private Boolean negativeMarksEnabled;
    private Double negativeMarksValue;
    private Boolean randomizeQuestions;
    private Boolean randomizeOptions;
    private Boolean showResultsImmediately;
    private Boolean autoSubmit;
    private Long departmentId;
    private String departmentName;
    private Long semesterId;
    private Integer semesterNumber;
    private Long subjectId;
    private String subjectName;
    private Long createdById;
    private String createdByName;
    private Integer questionCount;
    private Integer studentCount;
    private LocalDateTime createdAt;
}
