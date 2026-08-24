package com.evalorithm.service.impl;

import com.evalorithm.dto.request.TopicRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.TopicResponse;
import com.evalorithm.entity.Topic;
import com.evalorithm.entity.Unit;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.TopicRepository;
import com.evalorithm.repository.UnitRepository;
import com.evalorithm.service.TopicService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final UnitRepository unitRepository;

    @Override
    public PageResponse<TopicResponse> getAll(Pageable pageable) {
        Page<Topic> page = topicRepository.findAll(pageable);
        List<TopicResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public TopicResponse getById(Long id) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));
        return mapToResponse(topic);
    }

    @Override
    public List<TopicResponse> getByUnit(Long unitId) {
        return topicRepository.findByUnitId(unitId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public TopicResponse create(TopicRequest request) {
        Unit unit = unitRepository.findById(request.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", request.getUnitId()));

        Topic topic = Topic.builder()
                .name(request.getName())
                .unit(unit)
                .description(request.getDescription())
                .keywords(request.getKeywords())
                .build();

        topic = topicRepository.save(topic);
        return mapToResponse(topic);
    }

    @Override
    @Transactional
    public TopicResponse update(Long id, TopicRequest request) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Topic", "id", id));

        if (request.getName() != null) topic.setName(request.getName());
        if (request.getUnitId() != null) {
            Unit unit = unitRepository.findById(request.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", "id", request.getUnitId()));
            topic.setUnit(unit);
        }
        if (request.getDescription() != null) topic.setDescription(request.getDescription());
        if (request.getKeywords() != null) topic.setKeywords(request.getKeywords());

        topic = topicRepository.save(topic);
        return mapToResponse(topic);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!topicRepository.existsById(id)) {
            throw new ResourceNotFoundException("Topic", "id", id);
        }
        topicRepository.deleteById(id);
    }

    private TopicResponse mapToResponse(Topic topic) {
        return TopicResponse.builder()
                .id(topic.getId())
                .name(topic.getName())
                .unitId(topic.getUnit().getId())
                .unitName(topic.getUnit().getName())
                .description(topic.getDescription())
                .keywords(topic.getKeywords())
                .createdAt(topic.getCreatedAt())
                .build();
    }
}
