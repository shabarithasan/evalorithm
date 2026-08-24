package com.evalorithm.service;

import java.util.Map;

public interface AdminMonitoringService {

    Long getOnlineUsers();

    Map<String, Object> getSystemHealth();

    Map<String, Object> getAPIHealth();

    Map<String, Object> getDatabaseHealth();

    Map<String, Object> getStorageUsage();
}
