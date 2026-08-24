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
public class SubjectResponse {

    private Long id;
    private String code;
    private String name;
    private Long departmentId;
    private String departmentName;
    private Long semesterId;
    private Integer semesterNumber;
    private Integer credits;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
}
