package com.evalorithm.service.impl;

import com.evalorithm.dto.request.QuestionCategoryRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.QuestionCategoryResponse;
import com.evalorithm.entity.QuestionCategory;
import com.evalorithm.enums.Status;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.QuestionCategoryRepository;
import com.evalorithm.service.QuestionCategoryService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionCategoryServiceImpl implements QuestionCategoryService {

    private final QuestionCategoryRepository questionCategoryRepository;

    @Override
    public PageResponse<QuestionCategoryResponse> getAll(Pageable pageable, String search) {
        Page<QuestionCategory> page;
        if (search != null && !search.isEmpty()) {
            page = questionCategoryRepository.searchByName(search, pageable);
        } else {
            page = questionCategoryRepository.findAll(pageable);
        }
        List<QuestionCategoryResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public QuestionCategoryResponse getById(Long id) {
        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionCategory", "id", id));
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public QuestionCategoryResponse create(QuestionCategoryRequest request) {
        if (questionCategoryRepository.existsByCategoryName(request.getCategoryName())) {
            throw new BadRequestException("Category name already exists");
        }

        QuestionCategory category = QuestionCategory.builder()
                .categoryName(request.getCategoryName())
                .description(request.getDescription())
                .status(request.getStatus() != null ? request.getStatus() : Status.ACTIVE)
                .build();

        category = questionCategoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public QuestionCategoryResponse update(Long id, QuestionCategoryRequest request) {
        QuestionCategory category = questionCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionCategory", "id", id));

        if (request.getCategoryName() != null) category.setCategoryName(request.getCategoryName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getStatus() != null) category.setStatus(request.getStatus());

        category = questionCategoryRepository.save(category);
        return mapToResponse(category);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!questionCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("QuestionCategory", "id", id);
        }
        questionCategoryRepository.deleteById(id);
    }

    private QuestionCategoryResponse mapToResponse(QuestionCategory category) {
        return QuestionCategoryResponse.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .description(category.getDescription())
                .status(category.getStatus())
                .questionCount(category.getQuestions() != null ? (long) category.getQuestions().size() : 0L)
                .createdAt(category.getCreatedAt())
                .build();
    }
}
