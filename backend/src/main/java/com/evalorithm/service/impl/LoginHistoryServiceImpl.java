package com.evalorithm.service.impl;

import com.evalorithm.dto.response.LoginHistoryResponse;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.entity.LoginHistory;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.LoginHistoryRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.LoginHistoryService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void recordLogin(Long userId, String ip, String device, String browser, boolean success) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        LoginHistory history = LoginHistory.builder()
                .user(user)
                .loginTime(LocalDateTime.now())
                .ipAddress(ip)
                .deviceInfo(device)
                .browser(browser)
                .isSuccessful(success)
                .build();

        loginHistoryRepository.save(history);
    }

    @Override
    @Transactional
    public void recordLogout(Long userId) {
        List<LoginHistory> histories = loginHistoryRepository.findByLogoutTimeIsNullAndIsSuccessfulTrue();
        histories.stream()
                .filter(h -> h.getUser().getId().equals(userId))
                .findFirst()
                .ifPresent(h -> {
                    h.setLogoutTime(LocalDateTime.now());
                    loginHistoryRepository.save(h);
                });
    }

    @Override
    public PageResponse<LoginHistoryResponse> getLoginHistory(Long userId, Pageable pageable) {
        Page<LoginHistory> page = loginHistoryRepository.findByUserIdOrderByLoginTimeDesc(userId, pageable);
        List<LoginHistoryResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    public List<LoginHistoryResponse> getActiveUsers() {
        return loginHistoryRepository.findByLogoutTimeIsNullAndIsSuccessfulTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Map<String, Object> getLoginStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long totalLogins = loginHistoryRepository.countByLoginTimeAfter(todayStart);
        long failedLogins = loginHistoryRepository.findByIsSuccessful(false).stream()
                .filter(h -> h.getLoginTime().isAfter(todayStart))
                .count();
        long uniqueUsers = loginHistoryRepository.findByLogoutTimeIsNullAndIsSuccessfulTrue().stream()
                .map(h -> h.getUser().getId())
                .distinct()
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLoginsToday", totalLogins);
        stats.put("failedLoginsToday", failedLogins);
        stats.put("activeUsers", uniqueUsers);
        return stats;
    }

    private LoginHistoryResponse mapToResponse(LoginHistory history) {
        return LoginHistoryResponse.builder()
                .id(history.getId())
                .userName(history.getUser().getFirstName() + " " + history.getUser().getLastName())
                .loginTime(history.getLoginTime())
                .logoutTime(history.getLogoutTime())
                .ipAddress(history.getIpAddress())
                .deviceInfo(history.getDeviceInfo())
                .browser(history.getBrowser())
                .isSuccessful(history.getIsSuccessful())
                .build();
    }
}
