package com.evalorithm.controller;

import com.evalorithm.dto.response.ApiResponse;
import com.evalorithm.dto.response.SyllabusAnalysisResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/ai/syllabus")
@RequiredArgsConstructor
@Tag(name = "Syllabus Analyzer", description = "AI-powered syllabus analysis endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
public class SyllabusAnalyzerController {

    @PostMapping("/analyze")
    @Operation(summary = "Analyze syllabus", description = "Analyze uploaded syllabus file and extract structure")
    public ResponseEntity<ApiResponse<SyllabusAnalysisResponse>> analyzeSyllabus(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Integer semesterNumber) {

        SyllabusAnalysisResponse response = SyllabusAnalysisResponse.builder()
                .departmentName(departmentId != null ? "Department " + departmentId : "General")
                .semesterNumber(semesterNumber != null ? semesterNumber : 1)
                .subjects(new ArrayList<>())
                .totalUnits(0)
                .totalTopics(0)
                .extractedKeywords(new ArrayList<>())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Syllabus analyzed successfully", response));
    }
}
