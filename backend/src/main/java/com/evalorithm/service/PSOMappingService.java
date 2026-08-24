package com.evalorithm.service;

import com.evalorithm.dto.request.PSOMappingRequest;
import com.evalorithm.dto.response.COMappingResponse;

import java.util.List;

public interface PSOMappingService {

    void mapCOToPSO(PSOMappingRequest request);

    void removeMapping(Long mappingId);

    List<COMappingResponse> getPSOsByDepartment(Long departmentId);
}
