package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExamDetailResponse extends ExamResponse {

    private List<ExamQuestionResponse> examQuestions;
    private List<ExamStudentResponse> assignedStudents;
}
