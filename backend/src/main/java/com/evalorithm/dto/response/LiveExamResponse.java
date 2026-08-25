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
public class LiveExamResponse {

    private Long examId;
    private String title;
    private Integer durationMinutes;
    private Integer totalMarks;
    private List<LiveExamQuestion> questions;
    private Integer currentQuestionIndex;
    private Long timeRemainingSeconds;
    private Long attemptId;
    private Boolean preventTabSwitch;
    private Boolean fullscreenRequired;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LiveExamQuestion {
        private Long examQuestionId;
        private Integer orderNumber;
        private QuestionType questionType;
        private String questionTitle;
        private String questionDescription;
        private Integer marks;
        private List<LiveExamOption> options;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LiveExamOption {
        private String optionLabel;
        private String optionText;
    }
}
