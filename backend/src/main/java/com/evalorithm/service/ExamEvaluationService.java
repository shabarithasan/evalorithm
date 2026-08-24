package com.evalorithm.service;

import com.evalorithm.entity.ExamResult;

public interface ExamEvaluationService {

    void evaluateAttempt(Long attemptId);

    ExamResult calculateResult(Long attemptId);

    String getGrade(double percentage);
}
