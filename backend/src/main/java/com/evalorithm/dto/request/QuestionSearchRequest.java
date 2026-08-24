package com.evalorithm.dto.request;

import com.evalorithm.enums.BloomLevel;
import com.evalorithm.enums.QuestionDifficulty;
import com.evalorithm.enums.QuestionStatus;
import com.evalorithm.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionSearchRequest {

    private Long departmentId;

    private Long semesterId;

    private Long subjectId;

    private Long unitId;

    private Long topicId;

    private QuestionType questionType;

    private QuestionDifficulty difficulty;

    private BloomLevel bloomLevel;

    private QuestionStatus status;

    private Long categoryId;

    private Long createdBy;

    private String searchTerm;
}
