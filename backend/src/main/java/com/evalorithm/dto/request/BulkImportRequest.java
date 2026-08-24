package com.evalorithm.dto.request;

import com.evalorithm.enums.QuestionDifficulty;
import com.evalorithm.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportRequest {

    private Long departmentId;

    private Long semesterId;

    private Long subjectId;

    private Long unitId;

    private Long topicId;

    private QuestionType defaultType;

    private QuestionDifficulty defaultDifficulty;
}
