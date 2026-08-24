package com.evalorithm.service.impl;

import com.evalorithm.dto.response.ExamAttendanceResponse;
import com.evalorithm.entity.*;
import com.evalorithm.enums.AttendanceStatus;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.ExamAttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamAttendanceServiceImpl implements ExamAttendanceService {

    private final ExamAttendanceRepository examAttendanceRepository;
    private final ExamRepository examRepository;
    private final StudentProfileRepository studentProfileRepository;

    @Override
    @Transactional
    public void markAttendance(Long examId, Long studentId, AttendanceStatus status) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));
        StudentProfile student = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "id", studentId));

        ExamAttendance attendance = ExamAttendance.builder()
                .exam(exam)
                .studentProfile(student)
                .status(status)
                .joinTime(status != AttendanceStatus.ABSENT ? LocalDateTime.now() : null)
                .build();
        examAttendanceRepository.save(attendance);
    }

    @Override
    public List<ExamAttendanceResponse> getAttendance(Long examId) {
        List<ExamAttendance> attendanceList = examAttendanceRepository.findByExamId(examId);
        return attendanceList.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateAttendance(Long examId, Long studentId, AttendanceStatus status) {
        List<ExamAttendance> attendanceList = examAttendanceRepository.findByExamId(examId);
        ExamAttendance attendance = attendanceList.stream()
                .filter(a -> a.getStudentProfile().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttendance", "examId/studentId", examId + "/" + studentId));

        attendance.setStatus(status);
        if (status == AttendanceStatus.LEFT_EARLY || status == AttendanceStatus.COMPLETED) {
            attendance.setLeaveTime(LocalDateTime.now());
        }
        examAttendanceRepository.save(attendance);
    }

    private ExamAttendanceResponse mapToResponse(ExamAttendance attendance) {
        StudentProfile sp = attendance.getStudentProfile();
        User user = sp.getUser();
        String studentName = (user.getFirstName() != null ? user.getFirstName() : "") +
                (user.getLastName() != null ? " " + user.getLastName() : "");

        return ExamAttendanceResponse.builder()
                .id(attendance.getId())
                .studentName(studentName.trim())
                .registerNumber(sp.getRegisterNumber())
                .status(attendance.getStatus())
                .joinTime(attendance.getJoinTime())
                .leaveTime(attendance.getLeaveTime())
                .build();
    }
}
