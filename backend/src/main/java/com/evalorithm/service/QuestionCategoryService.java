package com.evalorithm.service;

import com.evalorithm.dto.request.QuestionCategoryRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.QuestionCategoryResponse;
import org.springframework.data.domain.Pageable;

public interface QuestionCategoryService {

    PageResponse<QuestionCategoryResponse> getAll(Pageable pageable, String search);

    QuestionCategoryResponse getById(Long id);

    QuestionCategoryResponse create(QuestionCategoryRequest request);

    QuestionCategoryResponse update(Long id, QuestionCategoryRequest request);

    void delete(Long id);
}
