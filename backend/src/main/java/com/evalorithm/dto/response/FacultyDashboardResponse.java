package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacultyDashboardResponse {

    private int assignedSubjectsCount;
    @Builder.Default
    private int questionCount = 0;
    @Builder.Default
    private int pendingQuestions = 0;
}
