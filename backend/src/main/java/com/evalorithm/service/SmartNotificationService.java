package com.evalorithm.service;

import com.evalorithm.dto.response.SmartNotificationResponse;

import java.util.List;

public interface SmartNotificationService {

    List<SmartNotificationResponse> generateSmartNotifications(Long studentId);

    List<SmartNotificationResponse> getSmartNotifications(Long studentId);
}
