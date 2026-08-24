package com.evalorithm.dto.request;

import com.evalorithm.enums.ExamType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ExamRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Exam type is required")
    private ExamType examType;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @NotNull(message = "Duration is required")
    private Integer durationMinutes;

    @NotNull(message = "Total marks is required")
    private Integer totalMarks;

    @NotNull(message = "Passing marks is required")
    private Integer passingMarks;

    private Integer maxAttempts;

    private Boolean negativeMarksEnabled;

    private Double negativeMarksValue;

    private Boolean randomizeQuestions;

    private Boolean randomizeOptions;

    private Boolean showResultsImmediately;

    private Boolean autoSubmit;

    private Boolean fullscreenRequired;

    private Boolean preventTabSwitch;

    private Long departmentId;

    private Long semesterId;

    private Long subjectId;

    private List<ExamQuestionRequest> examQuestions;

    private List<Long> assignStudentIds;

    private List<Long> assignDepartmentIds;

    private List<Long> assignSemesterIds;
}
