package com.evalorithm.service.impl;

import com.evalorithm.dto.request.COMappingRequest;
import com.evalorithm.dto.response.COMappingResponse;
import com.evalorithm.entity.COMapping;
import com.evalorithm.entity.CourseOutcome;
import com.evalorithm.entity.Question;
import com.evalorithm.exception.BadRequestException;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.COMappingRepository;
import com.evalorithm.repository.CourseOutcomeRepository;
import com.evalorithm.repository.QuestionRepository;
import com.evalorithm.service.COMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class COMappingServiceImpl implements COMappingService {

    private final COMappingRepository coMappingRepository;
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final QuestionRepository questionRepository;

    @Override
    @Transactional
    public COMappingResponse mapQuestionToCO(COMappingRequest request) {
        CourseOutcome co = courseOutcomeRepository.findById(request.getCoId())
                .orElseThrow(() -> new ResourceNotFoundException("CourseOutcome", "id", request.getCoId()));

        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", request.getQuestionId()));

        boolean exists = coMappingRepository.findByCoId(request.getCoId()).stream()
                .anyMatch(m -> m.getQuestion().getId().equals(request.getQuestionId()));
        if (exists) {
            throw new BadRequestException("Question is already mapped to this CO");
        }

        COMapping mapping = COMapping.builder()
                .co(co)
                .question(question)
                .questionType(question.getQuestionType() != null ? question.getQuestionType().name() : null)
                .weightage(request.getWeightage() != null ? request.getWeightage() : 100.0)
                .build();

        mapping = coMappingRepository.save(mapping);
        return mapToResponse(mapping);
    }

    @Override
    @Transactional
    public void removeMapping(Long mappingId) {
        if (!coMappingRepository.existsById(mappingId)) {
            throw new ResourceNotFoundException("COMapping", "id", mappingId);
        }
        coMappingRepository.deleteById(mappingId);
    }

    @Override
    public List<COMappingResponse> getCOsBySubject(Long subjectId) {
        List<COMapping> mappings = coMappingRepository.findByCoSubjectId(subjectId);
        return mappings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<COMappingResponse> getMappedQuestions(Long coId) {
        return coMappingRepository.findByCoId(coId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    private COMappingResponse mapToResponse(COMapping mapping) {
        return COMappingResponse.builder()
                .id(mapping.getId())
                .coCode(mapping.getCo() != null ? mapping.getCo().getCode() : null)
                .questionTitle(mapping.getQuestion() != null ? mapping.getQuestion().getTitle() : null)
                .questionType(mapping.getQuestionType())
                .weightage(mapping.getWeightage())
                .build();
    }
}
