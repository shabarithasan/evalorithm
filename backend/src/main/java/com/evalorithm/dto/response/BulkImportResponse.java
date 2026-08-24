package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BulkImportResponse {

    private Integer totalRows;
    private Integer successfulImports;
    private Integer failedImports;
    private List<String> errors;
    private List<QuestionResponse> importedQuestions;
}
