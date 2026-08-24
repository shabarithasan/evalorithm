package com.evalorithm.dto.response;

import com.evalorithm.enums.MediaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionMediaResponse {

    private Long id;
    private String fileName;
    private String fileUrl;
    private MediaType fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
}
