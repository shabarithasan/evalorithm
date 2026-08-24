package com.evalorithm.dto.request;

import com.evalorithm.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionApprovalRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotNull(message = "Approval status is required")
    private ApprovalStatus status;

    private String comments;
}
