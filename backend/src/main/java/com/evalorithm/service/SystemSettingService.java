package com.evalorithm.service;

import com.evalorithm.dto.request.SystemSettingRequest;
import com.evalorithm.dto.response.PageResponse;
import com.evalorithm.dto.response.SystemSettingResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SystemSettingService {

    SystemSettingResponse getSetting(String key);

    List<SystemSettingResponse> getSettingsByCategory(String category);

    PageResponse<SystemSettingResponse> getAllSettings(Pageable pageable);

    SystemSettingResponse updateSetting(SystemSettingRequest request, Long userId);

    SystemSettingResponse getOrCreate(String key, String defaultValue, String category);
}
