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
public class QuestionVersionResponse {

    private Long id;
    private Integer versionNumber;
    private String updatedByName;
    private String changeDescription;
    private LocalDateTime createdAt;
}
