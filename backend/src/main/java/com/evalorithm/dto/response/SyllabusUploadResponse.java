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
public class SyllabusUploadResponse {

    private List<TopicStructure> extractedTopics;
    private String message;
    private List<SavedUnit> savedUnits;
    private List<AIGeneratedQuestion> generatedQuestions;
    private CreatedExamInfo createdExam;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopicStructure {
        private String unitName;
        private String unitNumber;
        private List<String> topics;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SavedUnit {
        private Long unitId;
        private String unitName;
        private Integer unitNumber;
        private List<Long> topicIds;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AIGeneratedQuestion {
        private Long questionId;
        private String questionText;
        private String questionType;
        private String difficulty;
        private String bloomLevel;
        private String topicName;
        private String unitName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatedExamInfo {
        private Long examId;
        private String examTitle;
        private Integer totalQuestions;
        private Integer totalMarks;
        private String examType;
        private String status;
    }
}
