package com.evalorithm.service;

import com.evalorithm.dto.request.AttainmentRequest;
import com.evalorithm.dto.response.AttainmentDashboardResponse;
import com.evalorithm.dto.response.AttainmentResponse;

import java.util.List;

public interface AttainmentService {

    AttainmentResponse calculateAttainment(AttainmentRequest request);

    AttainmentDashboardResponse getAttainmentDashboard(Long departmentId, String academicYear);

    List<AttainmentResponse> getAttainmentBySubject(Long subjectId, Long semesterId);

    byte[] exportAttainmentReport(Long departmentId, String academicYear, String format);
}
