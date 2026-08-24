package com.evalorithm.service;

import com.evalorithm.dto.request.StudentAnswerRequest;
import com.evalorithm.dto.response.LiveExamResponse;
import com.evalorithm.dto.response.SubmitExamResponse;

public interface ExamTakingService {

    LiveExamResponse startExam(Long examId, Long studentId, String ip, String userAgent);

    LiveExamResponse getExamQuestion(Long attemptId, int questionIndex);

    void saveAnswer(Long attemptId, StudentAnswerRequest request);

    SubmitExamResponse submitExam(Long attemptId);

    LiveExamResponse resumeExam(Long attemptId);

    LiveExamResponse getExamStatus(Long examId, Long studentId);
}
