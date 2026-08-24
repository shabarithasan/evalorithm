package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDashboardResponse {

    private int enrolledSubjectsCount;
    @Builder.Default
    private int upcomingExams = 0;
    @Builder.Default
    private List<Object> recentResults = new ArrayList<>();
}
