package com.evalorithm.service;

import com.evalorithm.dto.request.FeedbackRequest;
import com.evalorithm.dto.response.FeedbackResponse;

import java.util.List;

public interface FeedbackService {

    FeedbackResponse submitFeedback(FeedbackRequest request, Long fromUserId);

    List<FeedbackResponse> getFeedbackForUser(Long userId);

    List<FeedbackResponse> getFeedbackByUser(Long userId);

    List<FeedbackResponse> getSubjectFeedback(Long subjectId);

    Double getAverageRating(Long subjectId);
}
