package com.evalorithm.service;

import com.evalorithm.entity.ExamNotification;
import com.evalorithm.dto.response.PageResponse;

import java.util.List;

public interface ExamNotificationService {

    void sendExamPublishedNotification(Long examId);

    void sendExamReminder(Long examId);

    void sendResultPublishedNotification(Long examId);

    List<ExamNotification> getUserNotifications(Long userId);

    long getUnreadCount(Long userId);

    void markAsRead(Long notificationId);
}
