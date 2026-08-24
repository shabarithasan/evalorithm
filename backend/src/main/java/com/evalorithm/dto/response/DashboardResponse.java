package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long totalDepartments;
    private long totalSubjects;
    private long totalFaculty;
    private long totalStudents;
    @Builder.Default
    private long totalQuestions = 0;
}
