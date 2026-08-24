package com.evalorithm.service.impl;

import com.evalorithm.dto.request.FeedbackRequest;
import com.evalorithm.dto.response.FeedbackResponse;
import com.evalorithm.entity.*;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.*;
import com.evalorithm.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final ExamRepository examRepository;

    @Override
    @Transactional
    public FeedbackResponse submitFeedback(FeedbackRequest request, Long fromUserId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", fromUserId));

        User toUser = null;
        if (request.getToUserId() != null) {
            toUser = userRepository.findById(request.getToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getToUserId()));
        }

        Subject subject = null;
        if (request.getSubjectId() != null) {
            subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));
        }

        Exam exam = null;
        if (request.getExamId() != null) {
            exam = examRepository.findById(request.getExamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", request.getExamId()));
        }

        Feedback feedback = Feedback.builder()
                .feedbackType(request.getFeedbackType())
                .fromUser(fromUser)
                .toUser(toUser)
                .subject(subject)
                .exam(exam)
                .rating(request.getRating())
                .comment(request.getComment())
                .suggestions(request.getSuggestions())
                .isAnonymous(request.isAnonymous())
                .build();

        feedback = feedbackRepository.save(feedback);
        return mapToResponse(feedback);
    }

    @Override
    public List<FeedbackResponse> getFeedbackForUser(Long userId) {
        return feedbackRepository.findByToUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FeedbackResponse> getFeedbackByUser(Long userId) {
        return feedbackRepository.findByFromUserId(userId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<FeedbackResponse> getSubjectFeedback(Long subjectId) {
        return feedbackRepository.findBySubjectId(subjectId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Double getAverageRating(Long subjectId) {
        Double avg = feedbackRepository.avgRatingBySubjectId(subjectId);
        return avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0;
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        return FeedbackResponse.builder()
                .id(feedback.getId())
                .feedbackType(feedback.getFeedbackType().name())
                .fromUserName(feedback.getIsAnonymous() ? "Anonymous" :
                        feedback.getFromUser().getFirstName() + " " + feedback.getFromUser().getLastName())
                .toUserName(feedback.getToUser() != null ?
                        feedback.getToUser().getFirstName() + " " + feedback.getToUser().getLastName() : null)
                .subjectName(feedback.getSubject() != null ? feedback.getSubject().getName() : null)
                .rating(feedback.getRating())
                .comment(feedback.getComment())
                .suggestions(feedback.getSuggestions())
                .isAnonymous(feedback.getIsAnonymous())
                .createdAt(feedback.getCreatedAt())
                .build();
    }
}
