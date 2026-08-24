package com.evalorithm.service.impl;

import com.evalorithm.dto.response.QuestionStatisticsResponse;
import com.evalorithm.entity.QuestionStatistics;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.QuestionStatisticsRepository;
import com.evalorithm.service.QuestionStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QuestionStatisticsServiceImpl implements QuestionStatisticsService {

    private final QuestionStatisticsRepository questionStatisticsRepository;

    @Override
    @Transactional
    public void recordView(Long questionId) {
        QuestionStatistics stats = questionStatisticsRepository.findByQuestionId(questionId)
                .orElseGet(() -> {
                    QuestionStatistics newStats = QuestionStatistics.builder()
                            .question(null)
                            .viewCount(0)
                            .usageCount(0)
                            .correctCount(0)
                            .wrongCount(0)
                            .build();
                    return newStats;
                });
        stats.setViewCount(stats.getViewCount() + 1);
        questionStatisticsRepository.save(stats);
    }

    @Override
    @Transactional
    public void recordUsage(Long questionId, boolean correct) {
        QuestionStatistics stats = questionStatisticsRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionStatistics", "questionId", questionId));
        stats.setUsageCount(stats.getUsageCount() + 1);
        if (correct) {
            stats.setCorrectCount(stats.getCorrectCount() + 1);
        } else {
            stats.setWrongCount(stats.getWrongCount() + 1);
        }
        stats.setLastUsedAt(LocalDateTime.now());
        questionStatisticsRepository.save(stats);
    }

    @Override
    public QuestionStatisticsResponse getStatistics(Long questionId) {
        QuestionStatistics stats = questionStatisticsRepository.findByQuestionId(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionStatistics", "questionId", questionId));

        int totalAttempts = stats.getCorrectCount() + stats.getWrongCount();
        double correctPercentage = totalAttempts > 0 ? (double) stats.getCorrectCount() / totalAttempts * 100 : 0.0;
        double wrongPercentage = totalAttempts > 0 ? (double) stats.getWrongCount() / totalAttempts * 100 : 0.0;

        return QuestionStatisticsResponse.builder()
                .id(stats.getId())
                .viewCount(stats.getViewCount())
                .usageCount(stats.getUsageCount())
                .correctCount(stats.getCorrectCount())
                .wrongCount(stats.getWrongCount())
                .correctPercentage(correctPercentage)
                .wrongPercentage(wrongPercentage)
                .lastUsedAt(stats.getLastUsedAt())
                .build();
    }
}
