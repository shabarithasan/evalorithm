package com.evalorithm.service.impl;

import com.evalorithm.dto.request.BulkImportRequest;
import com.evalorithm.dto.request.QuestionRequest;
import com.evalorithm.dto.response.BulkImportResponse;
import com.evalorithm.dto.response.QuestionResponse;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.service.BulkImportService;
import com.evalorithm.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BulkImportServiceImpl implements BulkImportService {

    private final QuestionService questionService;

    @Override
    public BulkImportResponse importFromExcel(MultipartFile file, BulkImportRequest request) {
        List<QuestionRequest> questions = parseExcel(file, request);
        return processImport(questions);
    }

    @Override
    public BulkImportResponse importFromCsv(MultipartFile file, BulkImportRequest request) {
        List<QuestionRequest> questions = parseCsv(file, request);
        return processImport(questions);
    }

    private List<QuestionRequest> parseExcel(MultipartFile file, BulkImportRequest request) {
        List<QuestionRequest> questions = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                QuestionRequest questionRequest = new QuestionRequest();
                questionRequest.setTitle(getCellValue(row, 0));
                questionRequest.setDescription(getCellValue(row, 1));
                questionRequest.setMarks(parseIntOrNull(getCellValue(row, 2)));
                questionRequest.setEstimatedTime(parseIntOrNull(getCellValue(row, 3)));
                questionRequest.setExplanation(getCellValue(row, 4));
                questionRequest.setReference(getCellValue(row, 5));
                questionRequest.setDifficulty(request.getDefaultDifficulty());
                questionRequest.setQuestionType(request.getDefaultType());
                questionRequest.setDepartmentId(request.getDepartmentId());
                questionRequest.setSemesterId(request.getSemesterId());
                questionRequest.setSubjectId(request.getSubjectId());
                questionRequest.setUnitId(request.getUnitId());
                questionRequest.setTopicId(request.getTopicId());

                questions.add(questionRequest);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to parse Excel file: " + e.getMessage());
        }
        return questions;
    }

    private List<QuestionRequest> parseCsv(MultipartFile file, BulkImportRequest request) {
        List<QuestionRequest> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                QuestionRequest questionRequest = new QuestionRequest();
                questionRequest.setTitle(parts.length > 0 ? parts[0].trim() : "");
                questionRequest.setDescription(parts.length > 1 ? parts[1].trim() : "");
                questionRequest.setMarks(parts.length > 2 ? parseIntOrNull(parts[2].trim()) : null);
                questionRequest.setEstimatedTime(parts.length > 3 ? parseIntOrNull(parts[3].trim()) : null);
                questionRequest.setExplanation(parts.length > 4 ? parts[4].trim() : null);
                questionRequest.setReference(parts.length > 5 ? parts[5].trim() : null);
                questionRequest.setDifficulty(request.getDefaultDifficulty());
                questionRequest.setQuestionType(request.getDefaultType());
                questionRequest.setDepartmentId(request.getDepartmentId());
                questionRequest.setSemesterId(request.getSemesterId());
                questionRequest.setSubjectId(request.getSubjectId());
                questionRequest.setUnitId(request.getUnitId());
                questionRequest.setTopicId(request.getTopicId());

                questions.add(questionRequest);
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to parse CSV file: " + e.getMessage());
        }
        return questions;
    }

    private BulkImportResponse processImport(List<QuestionRequest> questions) {
        List<QuestionResponse> importedQuestions = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int failedCount = 0;

        for (int i = 0; i < questions.size(); i++) {
            try {
                QuestionResponse response = questionService.createQuestion(questions.get(i), 1L);
                importedQuestions.add(response);
            } catch (Exception e) {
                failedCount++;
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
                log.error("Failed to import question at row {}: {}", i + 1, e.getMessage());
            }
        }

        return BulkImportResponse.builder()
                .totalRows(questions.size())
                .successfulImports(importedQuestions.size())
                .failedImports(failedCount)
                .errors(errors)
                .importedQuestions(importedQuestions)
                .build();
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }

    private Integer parseIntOrNull(String value) {
        if (value == null || value.isEmpty()) return null;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
