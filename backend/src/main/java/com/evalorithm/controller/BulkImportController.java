package com.evalorithm.controller;

import com.evalorithm.dto.request.BulkImportRequest;
import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.BulkImportResponse;
import com.evalorithm.service.BulkImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/bulk-import")
@RequiredArgsConstructor
@Tag(name = "Bulk Import", description = "Bulk question import endpoints")
public class BulkImportController {

    private final BulkImportService bulkImportService;

    @PostMapping("/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Import questions from Excel", description = "Import questions from an Excel file (Admin/Faculty)")
    public ResponseEntity<ApiResponse<BulkImportResponse>> importFromExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String defaultType,
            @RequestParam(required = false) String defaultDifficulty) {

        BulkImportRequest request = buildImportRequest(departmentId, semesterId, subjectId, unitId, topicId, defaultType, defaultDifficulty);
        BulkImportResponse response = bulkImportService.importFromExcel(file, request);
        return ResponseEntity.ok(ApiResponse.success("Import completed", response));
    }

    @PostMapping("/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    @Operation(summary = "Import questions from CSV", description = "Import questions from a CSV file (Admin/Faculty)")
    public ResponseEntity<ApiResponse<BulkImportResponse>> importFromCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long semesterId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String defaultType,
            @RequestParam(required = false) String defaultDifficulty) {

        BulkImportRequest request = buildImportRequest(departmentId, semesterId, subjectId, unitId, topicId, defaultType, defaultDifficulty);
        BulkImportResponse response = bulkImportService.importFromCsv(file, request);
        return ResponseEntity.ok(ApiResponse.success("Import completed", response));
    }

    private BulkImportRequest buildImportRequest(Long departmentId, Long semesterId, Long subjectId,
                                                  Long unitId, Long topicId, String defaultType, String defaultDifficulty) {
        BulkImportRequest request = new BulkImportRequest();
        request.setDepartmentId(departmentId);
        request.setSemesterId(semesterId);
        request.setSubjectId(subjectId);
        request.setUnitId(unitId);
        request.setTopicId(topicId);
        if (defaultType != null) {
            request.setDefaultType(com.evalorithm.enums.QuestionType.valueOf(defaultType));
        }
        if (defaultDifficulty != null) {
            request.setDefaultDifficulty(com.evalorithm.enums.QuestionDifficulty.valueOf(defaultDifficulty));
        }
        return request;
    }
}
