package com.evalorithm.dto.request;

import com.evalorithm.enums.FeedbackType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    @NotNull(message = "Feedback type is required")
    private FeedbackType feedbackType;

    private Long toUserId;

    private Long subjectId;

    private Long examId;

    @NotNull(message = "Rating is required")
    private Integer rating;

    private String comment;

    private String suggestions;

    private boolean isAnonymous;
}
