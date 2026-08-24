package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.ExamAttendanceResponse;
import com.evalorithm.enums.AttendanceStatus;
import com.evalorithm.service.ExamAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/exam-attendance")
@RequiredArgsConstructor
@Tag(name = "Exam Attendance", description = "Exam attendance tracking endpoints")
public class ExamAttendanceController {

    private final ExamAttendanceService examAttendanceService;

    @PostMapping("/mark")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Mark attendance", description = "Mark student attendance for an exam (Admin/Faculty)")
    public ResponseEntity<ApiResponse<Void>> markAttendance(@RequestParam Long examId,
                                                             @RequestParam Long studentId,
                                                             @RequestParam AttendanceStatus status) {
        examAttendanceService.markAttendance(examId, studentId, status);
        return ResponseEntity.ok(ApiResponse.success("Attendance marked"));
    }

    @GetMapping("/exam/{examId}")
    @Operation(summary = "Get attendance", description = "Get attendance records for an exam")
    public ResponseEntity<ApiResponse<List<ExamAttendanceResponse>>> getAttendance(@PathVariable Long examId) {
        List<ExamAttendanceResponse> response = examAttendanceService.getAttendance(examId);
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved", response));
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Update attendance", description = "Update attendance status (Admin/Faculty)")
    public ResponseEntity<ApiResponse<Void>> updateAttendance(@RequestParam Long examId,
                                                               @RequestParam Long studentId,
                                                               @RequestParam AttendanceStatus status) {
        examAttendanceService.updateAttendance(examId, studentId, status);
        return ResponseEntity.ok(ApiResponse.success("Attendance updated"));
    }
}
