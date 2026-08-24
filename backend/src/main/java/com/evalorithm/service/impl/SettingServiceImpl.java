package com.evalorithm.service.impl;

import com.evalorithm.dto.request.SettingRequest;
import com.evalorithm.dto.response.SettingResponse;
import com.evalorithm.entity.Setting;
import com.evalorithm.exception.ResourceNotFoundException;
import com.evalorithm.repository.SettingRepository;
import com.evalorithm.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingServiceImpl implements SettingService {

    private final SettingRepository settingRepository;

    @Override
    public SettingResponse getSetting(String key) {
        Setting setting = settingRepository.findBySettingKey(key)
                .orElseThrow(() -> new ResourceNotFoundException("Setting", "key", key));
        return mapToResponse(setting);
    }

    @Override
    public List<SettingResponse> getAllSettings() {
        return settingRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public SettingResponse updateSetting(SettingRequest request) {
        Setting setting = settingRepository.findBySettingKey(request.getSettingKey())
                .orElse(Setting.builder()
                        .settingKey(request.getSettingKey())
                        .build());

        setting.setSettingValue(request.getSettingValue());
        setting.setDescription(request.getDescription());

        setting = settingRepository.save(setting);
        return mapToResponse(setting);
    }

    @Override
    @Transactional
    public String getOrCreateSetting(String key, String defaultValue) {
        Setting setting = settingRepository.findBySettingKey(key)
                .orElseGet(() -> settingRepository.save(Setting.builder()
                        .settingKey(key)
                        .settingValue(defaultValue)
                        .build()));
        return setting.getSettingValue();
    }

    private SettingResponse mapToResponse(Setting setting) {
        return SettingResponse.builder()
                .id(setting.getId())
                .settingKey(setting.getSettingKey())
                .settingValue(setting.getSettingValue())
                .description(setting.getDescription())
                .build();
    }
}
