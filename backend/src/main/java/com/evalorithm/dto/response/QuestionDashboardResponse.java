package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDashboardResponse {

    private Long totalQuestions;
    private Long approvedQuestions;
    private Long pendingQuestions;
    private Long rejectedQuestions;
    private Long recentlyAdded;
}
