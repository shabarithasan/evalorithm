package com.evalorithm.service;

import com.evalorithm.dto.request.SettingRequest;
import com.evalorithm.dto.response.SettingResponse;

import java.util.List;

public interface SettingService {

    SettingResponse getSetting(String key);

    List<SettingResponse> getAllSettings();

    SettingResponse updateSetting(SettingRequest request);

    String getOrCreateSetting(String key, String defaultValue);
}
