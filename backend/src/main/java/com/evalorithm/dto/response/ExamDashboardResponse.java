package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDashboardResponse {

    private Long totalExams;
    private Long activeExams;
    private Long scheduledExams;
    private Long completedExams;
    private Long draftExams;
}
