package com.evalorithm.dto.response;

import com.evalorithm.enums.AnswerStatus;
import com.evalorithm.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswerResponse {

    private Long id;
    private Long examQuestionId;
    private String questionTitle;
    private QuestionType questionType;
    private String selectedOptionLabel;
    private String textAnswer;
    private Boolean isCorrect;
    private Double marksAwarded;
    private Integer timeTakenSeconds;
    private AnswerStatus answerStatus;
}
