package com.evalorithm.dto.response;

import com.evalorithm.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SemesterResponse {

    private Long id;
    private Integer number;
    private Long departmentId;
    private String departmentName;
    private Status status;
    private LocalDateTime createdAt;
}
