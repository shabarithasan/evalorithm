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
public class TopicResponse {

    private Long id;
    private String name;
    private Long unitId;
    private String unitName;
    private String description;
    private String keywords;
    private LocalDateTime createdAt;
}
