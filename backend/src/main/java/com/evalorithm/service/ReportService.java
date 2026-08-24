package com.evalorithm.service;

import com.evalorithm.dto.request.ReportGenerateRequest;

public interface ReportService {

    byte[] generateReport(ReportGenerateRequest request);
}
