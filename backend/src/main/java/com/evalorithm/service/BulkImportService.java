package com.evalorithm.service;

import com.evalorithm.dto.request.BulkImportRequest;
import com.evalorithm.dto.response.BulkImportResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BulkImportService {

    BulkImportResponse importFromExcel(MultipartFile file, BulkImportRequest request);

    BulkImportResponse importFromCsv(MultipartFile file, BulkImportRequest request);
}
