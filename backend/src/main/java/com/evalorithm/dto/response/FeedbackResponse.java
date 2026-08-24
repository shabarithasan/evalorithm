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
public class FeedbackResponse {

    private Long id;
    private String feedbackType;
    private String fromUserName;
    private String toUserName;
    private String subjectName;
    private Integer rating;
    private String comment;
    private String suggestions;
    private Boolean isAnonymous;
    private LocalDateTime createdAt;
}
