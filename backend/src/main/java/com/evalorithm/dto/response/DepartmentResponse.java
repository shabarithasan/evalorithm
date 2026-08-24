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
public class DepartmentResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
}
