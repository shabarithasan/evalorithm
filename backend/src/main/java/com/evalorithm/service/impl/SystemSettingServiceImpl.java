package com.evalorithm.service.impl;

import com.evalorithm.dto.request.SystemSettingRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SystemSettingResponse;
import com.evalorithm.entity.SystemSetting;
import com.evalorithm.entity.User;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.SystemSettingRepository;
import com.evalorithm.repository.UserRepository;
import com.evalorithm.service.SystemSettingService;
import com.evalorithm.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemSettingServiceImpl implements SystemSettingService {

    private final SystemSettingRepository systemSettingRepository;
    private final UserRepository userRepository;

    @Override
    public SystemSettingResponse getSetting(String key) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("SystemSetting", "settingKey", key));
        return mapToResponse(setting);
    }

    @Override
    public List<SystemSettingResponse> getSettingsByCategory(String category) {
        return systemSettingRepository.findByCategory(category).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PageResponse<SystemSettingResponse> getAllSettings(Pageable pageable) {
        Page<SystemSetting> page = systemSettingRepository.findAll(pageable);
        List<SystemSettingResponse> content = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PaginationUtil.createPageResponse(page, content);
    }

    @Override
    @Transactional
    public SystemSettingResponse updateSetting(SystemSettingRequest request, Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        SystemSetting setting = systemSettingRepository.findBySettingKey(request.getSettingKey())
                .orElse(SystemSetting.builder()
                        .settingKey(request.getSettingKey())
                        .dataType("STRING")
                        .build());

        if (request.getSettingValue() != null) setting.setSettingValue(request.getSettingValue());
        if (request.getCategory() != null) setting.setCategory(request.getCategory());
        if (request.getDescription() != null) setting.setDescription(request.getDescription());
        setting.setUpdatedBy(user);
        setting.setUpdatedAt(LocalDateTime.now());

        setting = systemSettingRepository.save(setting);
        return mapToResponse(setting);
    }

    @Override
    @Transactional
    public SystemSettingResponse getOrCreate(String key, String defaultValue, String category) {
        SystemSetting setting = systemSettingRepository.findBySettingKey(key)
                .orElseGet(() -> {
                    SystemSetting newSetting = SystemSetting.builder()
                            .settingKey(key)
                            .settingValue(defaultValue)
                            .category(category)
                            .dataType("STRING")
                            .build();
                    return systemSettingRepository.save(newSetting);
                });
        return mapToResponse(setting);
    }

    private SystemSettingResponse mapToResponse(SystemSetting setting) {
        return SystemSettingResponse.builder()
                .id(setting.getId())
                .settingKey(setting.getSettingKey())
                .settingValue(setting.getSettingValue())
                .category(setting.getCategory())
                .description(setting.getDescription())
                .dataType(setting.getDataType())
                .updatedByName(setting.getUpdatedBy() != null ?
                        setting.getUpdatedBy().getFirstName() + " " + setting.getUpdatedBy().getLastName() : null)
                .build();
    }
}
