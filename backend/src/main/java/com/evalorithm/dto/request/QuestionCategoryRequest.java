package com.evalorithm.dto.request;

import com.evalorithm.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCategoryRequest {

    @NotBlank(message = "Category name is required")
    private String categoryName;

    private String description;

    private Status status;
}
