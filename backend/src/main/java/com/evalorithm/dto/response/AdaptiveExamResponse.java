package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdaptiveExamResponse {

    private Long examId;
    private String title;
    private Integer totalQuestions;
    private Integer durationMinutes;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdaptiveQuestion {
        private Long examQuestionId;
        private Integer orderNumber;
        private String questionTitle;
        private String questionDescription;
        private String questionType;
        private Integer marks;
        private String difficulty;
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