package com.evalorithm.service;

import com.evalorithm.dto.request.NotificationRequest;
import com.evalorithm.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    List<NotificationResponse> getUserNotifications(Long userId);

    NotificationResponse markAsRead(Long notificationId);

    void markAllAsRead(Long userId);

    long getUnreadCount(Long userId);

    NotificationResponse createNotification(NotificationRequest request);
}
