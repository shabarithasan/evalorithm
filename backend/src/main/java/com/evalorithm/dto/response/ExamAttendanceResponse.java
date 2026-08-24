package com.evalorithm.dto.response;

import com.evalorithm.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamAttendanceResponse {

    private Long id;
    private String studentName;
    private String registerNumber;
    private AttendanceStatus status;
    private LocalDateTime joinTime;
    private LocalDateTime leaveTime;
}
