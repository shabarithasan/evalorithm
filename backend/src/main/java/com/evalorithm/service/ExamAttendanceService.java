package com.evalorithm.service;

import com.evalorithm.enums.AttendanceStatus;
import com.evalorithm.dto.response.ExamAttendanceResponse;

import java.util.List;

public interface ExamAttendanceService {

    void markAttendance(Long examId, Long studentId, AttendanceStatus status);

    List<ExamAttendanceResponse> getAttendance(Long examId);

    void updateAttendance(Long examId, Long studentId, AttendanceStatus status);
}
