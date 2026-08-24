package com.evalorithm.service;

import com.evalorithm.dto.request.TopicRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.TopicResponse;

import java.util.List;
import org.springframework.data.domain.Pageable;

public interface TopicService {

    PageResponse<TopicResponse> getAll(Pageable pageable);

    TopicResponse getById(Long id);

    List<TopicResponse> getByUnit(Long unitId);

    TopicResponse create(TopicRequest request);

    TopicResponse update(Long id, TopicRequest request);

    void delete(Long id);
}
