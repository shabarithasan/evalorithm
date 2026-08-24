package com.evalorithm.service;

import com.evalorithm.dto.response.LoginHistoryResponse;
import com.evalorithm.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface LoginHistoryService {

    void recordLogin(Long userId, String ip, String device, String browser, boolean success);

    void recordLogout(Long userId);

    PageResponse<LoginHistoryResponse> getLoginHistory(Long userId, Pageable pageable);

    List<LoginHistoryResponse> getActiveUsers();

    Map<String, Object> getLoginStats();
}
