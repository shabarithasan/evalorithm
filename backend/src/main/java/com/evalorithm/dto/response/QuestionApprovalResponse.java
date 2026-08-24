package com.evalorithm.dto.response;

import com.evalorithm.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionApprovalResponse {

    private Long id;
    private String approverName;
    private ApprovalStatus status;
    private String comments;
    private LocalDateTime approvedAt;
}
