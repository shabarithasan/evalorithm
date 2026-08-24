package com.evalorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSettingResponse {

    private Long id;
    private String settingKey;
    private String settingValue;
    private String category;
    private String description;
    private String dataType;
    private String updatedByName;
}
