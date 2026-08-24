package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdaptiveSessionResponse {

    private Long id;
    private String subjectName;
    private String currentDifficulty;
    private Integer questionsAnswered;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double accuracy;
    private Integer streakCount;
    private Integer maxStreak;
    private Boolean isActive;
    private Double score;
    private LocalDateTime startTime;
}
