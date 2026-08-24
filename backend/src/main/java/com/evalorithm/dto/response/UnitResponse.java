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
public class UnitResponse {

    private Long id;
    private Integer number;
    private String name;
    private Long subjectId;
    private String subjectName;
    private String description;
    private LocalDateTime createdAt;
}
