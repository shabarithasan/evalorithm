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
public class SyllabusAnalysisResponse {

    private String departmentName;
    private Integer semesterNumber;
    private List<SubjectItem> subjects;
    private Integer totalUnits;
    private Integer totalTopics;
    private List<String> extractedKeywords;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubjectItem {
        private String subjectName;
        private List<UnitItem> units;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UnitItem {
        private String unitName;
        private Integer unitNumber;
        private List<String> topics;
    }
}
